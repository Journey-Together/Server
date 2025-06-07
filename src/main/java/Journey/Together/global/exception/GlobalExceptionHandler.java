package Journey.Together.global.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

import Journey.Together.global.common.DiscordErrorSender;
import Journey.Together.global.external.DiscordClient;
import Journey.Together.global.external.dto.DiscordMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final DiscordErrorSender discordErrorSender;

    @ExceptionHandler(ApplicationException.class)
    protected ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException e, HttpServletRequest request){
        log.error(e + " " + e.getErrorCode().toString());
        discordErrorSender.sendDiscordAlarm(e, new ServletWebRequest(request));
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(e.getErrorCode()));
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error(e.getMessage());
        discordErrorSender.sendDiscordAlarm(e, new ServletWebRequest(request));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
    }
}
