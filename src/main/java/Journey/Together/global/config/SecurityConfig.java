package Journey.Together.global.config;

import Journey.Together.global.security.ExceptionFilter;
import Journey.Together.global.security.jwt.JwtAccessDeniedHandler;
import Journey.Together.global.security.jwt.JwtAuthenticationEntryPoint;
import Journey.Together.global.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final ExceptionFilter exceptionFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    @Order(1)
    public SecurityFilterChain guestChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/v1/plan/guest/**",
                        "/v1/place/guest/**",
                        "/v1/plan/open",
                        "/v1/plan/search",
                        "/v1/place/search/**",
                        "/v1/place/kakao-map/**",
                        "/v1/place/main",
                        "/v1/place/review/guest/**",
                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                        "/actuator/**",
                        "/v1/auth/**", "/oauth2/**", "/login.html")
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                // 이 체인에는 JWT/Exception 필터를 '등록하지 않는다'
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    // 2) 나머지 보호 체인 (여기에만 JWT/Exception 필터 등록)
    @Bean
    @Order(2)
    public SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")  // 위에서 매칭되지 않은 나머지 경로들
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                // JWT 필터와 예외 필터는 '보호 체인'에만 단다
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionFilter, JwtFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/member/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(o -> o
                        .defaultSuccessUrl("/login-success")
                        .failureUrl("/login-failure")
                );

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
    public PasswordEncoder passwordEncoder(){
        // 비밀번호 암호화
        return new BCryptPasswordEncoder();
    }
}
