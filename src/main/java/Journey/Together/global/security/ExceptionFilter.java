package Journey.Together.global.security;

import Journey.Together.global.common.DiscordErrorSender;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import Journey.Together.global.external.DiscordClient;
import Journey.Together.global.external.dto.DiscordMessage;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final DiscordErrorSender discordErrorSender;

    // Jwt Filter에서 발생하는 Exception Handling
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ApplicationException e) {
            discordErrorSender.sendDiscordAlarm(e, new ServletWebRequest(request));
            setResponse(response);
        }
    }

    // Error 관련 응답 Response 생성 메소드
    private void setResponse(HttpServletResponse response) throws IOException{
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(ErrorCode.FORBIDDEN_EXCEPTION.getHttpStatus().value());

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.WRONG_TOKEN);
        String errorJson = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(errorJson);

    }
}
