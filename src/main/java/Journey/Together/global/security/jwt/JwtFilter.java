package Journey.Together.global.security.jwt;

import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        // String requestURI = request.getRequestURI();

        // 토큰이 존재할 경우, Authentication에 인증 정보 저장 및 로그 출력
        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // log.info("Security Context 인증 정보 저장: " + authentication.getEmail(), requestURI);
        }

        filterChain.doFilter(request, response);
    }

    // Request Header에서 토큰 조회 및 Bearer 문자열 제거 후 반환하는 메소드
    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        // Token 정보 존재 여부 및 Bearer 토큰인지 확인
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            // 블랙리스트 토큰인 경우
            String substringToken = token.substring(7);
            String value = redisClient.getValue(substringToken);
            if (value.equals("logout")) {
                throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
            }

            return substringToken;
        }

        return null;
    }
}
