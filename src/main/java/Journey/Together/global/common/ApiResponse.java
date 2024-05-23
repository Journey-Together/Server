package Journey.Together.global.common;


import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.exception.Success;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

	private final int code;
	private final String message;
	private T data;


	public static ApiResponse success(Success success){
		return new ApiResponse<>(success.getHttpStatusCode(), success.getMessage());
	}

	public static <T> ApiResponse<T> success(Success success, T data) {
		return new ApiResponse<T>(success.getHttpStatusCode(), success.getMessage(), data);
	}
}
