package Journey.Together.global.config;

import Journey.Together.global.security.ExceptionFilter;
import Journey.Together.global.security.jwt.JwtAccessDeniedHandler;
import Journey.Together.global.security.jwt.JwtAuthenticationEntryPoint;
import Journey.Together.global.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final ExceptionFilter exceptionFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final RequestMappingHandlerMapping handlerMapping;

    public SecurityConfig(
        JwtFilter jwtFilter,
        ExceptionFilter exceptionFilter,
        JwtAccessDeniedHandler jwtAccessDeniedHandler,
        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
        @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping
    ) {
        this.jwtFilter = jwtFilter;
        this.exceptionFilter = exceptionFilter;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.handlerMapping = handlerMapping;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS 허용, CSRF 비활성화
        http.cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement(
            (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));// Session 미사용

        // httpBasic, httpFormLogin 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable);

        // JWT 관련 필터 설정 및 예외 처리
        http.exceptionHandling((exceptionHandling) ->
            exceptionHandling
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionFilter, JwtFilter.class);

        // 요청 URI별 권한 설정
        http.authorizeHttpRequests(authorize -> {
            // Swagger 등 기본 허용
            authorize.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
            authorize.requestMatchers("/v1/auth/**", "/oauth2/**", "/login.html").permitAll();
            authorize.requestMatchers("/actuator/**").permitAll();

            // 동적으로 추출된 @PublicEndpoint 허용 처리
            for (String pattern : extractPublicUrls()) {
                authorize.requestMatchers(pattern).permitAll();
            }

            // 나머지 인증 필요
            authorize.anyRequest().authenticated();
        });



        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
            .defaultSuccessUrl("/login-success")
            .failureUrl("/login-failure"));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 인증정보 주고받도록 허용
        config.setAllowCredentials(true);
        // 허용할 주소
        config.setAllowedOriginPatterns(List.of("*"));
        // 허용할 HTTP Method
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // 허용할 헤더 정보
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호 암호화
        return new BCryptPasswordEncoder();
    }

    private Set<String> extractPublicUrls() {
        return handlerMapping.getHandlerMethods().entrySet().stream()
            .filter(entry -> {
                Method method = entry.getValue().getMethod();
                return method.isAnnotationPresent(PublicEndpoint.class)
                    || method.getDeclaringClass().isAnnotationPresent(PublicEndpoint.class);
            })
            .map(entry -> entry.getKey().getPathPatternsCondition())
            .filter(Objects::nonNull)
            .flatMap(condition -> condition.getPatternValues().stream())
            .collect(Collectors.toSet());
    }
}
