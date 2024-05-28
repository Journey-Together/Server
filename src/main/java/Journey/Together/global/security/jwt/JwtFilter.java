package Journey.Together.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        //jwt 유효성 검사를 하지않음
        if ("/v1/auth/sign-in".equals(requestURI) || "/actuator/health".equals(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);

        // 토큰이 존재할 경우, Authentication에 인증 정보 저장 및 로그 출력
        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    // Request Header에서 토큰 조회 및 Bearer 문자열 제거 후 반환하는 메소드
    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        // Token 정보 존재 여부 및 Bearer 토큰인지 확인
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            // 블랙리스트 토큰인 경우
            return token.substring(7);
        }

        return null;
    }
}
