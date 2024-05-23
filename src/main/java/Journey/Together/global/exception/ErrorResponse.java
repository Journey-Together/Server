package Journey.Together.global.exception;

import java.time.LocalDateTime;


public record ErrorResponse(
        LocalDateTime timestamp,
        Integer code,
        String message) {

    public ErrorResponse(ErrorCode errorcode) {
        this(LocalDateTime.now(), errorcode.getCode(), errorcode.getMessage());
    }

    public ErrorResponse(String message) {
        this(LocalDateTime.now(), ErrorCode.INTERNAL_SERVER_EXCEPTION.getCode(), message);
    }
}
