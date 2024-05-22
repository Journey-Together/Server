package Journey.Together.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Error {

	/**
	 * 404 NOT FOUND
	 */
	NOT_FOUND_USER_EXCEPTION(HttpStatus.NOT_FOUND, "찾을 수 없는 유저입니다."),
	NOT_FOUND_IMAGE_EXCEPTION(HttpStatus.NOT_FOUND, "s3 서비스에서 이미지를 찾을 수 없습니다."),

	/**
	 * 400 BAD REQUEST EXCEPTION
	 */
	BAD_REQUEST_ID(HttpStatus.BAD_REQUEST, "잘못된 id값입니다."),


	/**
	 * 401 UNAUTHORIZED EXCEPTION
	 */
	TOKEN_TIME_EXPIRED_EXCEPTION(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
	EXPIRED_APPLE_IDENTITY_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 아이덴티티 토큰입니다."),

	/**
	 * 403 FORBIDDEN EXCEPTION
	 */
	UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "리소스에 대한 권한이 없습니다."),
	INVALID_USER_ACCESS(HttpStatus.FORBIDDEN, "접근 권한이 없는 유저입니다."),

	/**
	 * 422 UNPROCESSABLE_ENTITY
	 */
	UNPROCESSABLE_ENTITY_DELETE_EXCEPTION(HttpStatus.UNPROCESSABLE_ENTITY, "서버에서 요청을 이해해 삭제하려는 도중 문제가 생겼습니다."),


	/**
	 * 500 INTERNAL_SERVER_ERROR
	 */
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러가 발생했습니다"),
	;

	private final HttpStatus httpStatus;
	private final String message;

	public int getErrorCode() {
		return httpStatus.value();
	}
}
