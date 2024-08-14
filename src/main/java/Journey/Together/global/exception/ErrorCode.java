package Journey.Together.global.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ErrorCode {

    // 1000: Success Case
    SUCCESS(HttpStatus.OK, 1000, "정상적인 요청입니다."),
    CREATED(HttpStatus.CREATED, 1001, "정상적으로 생성되었습니다."),

    // 2000: Common Error
    INTERNAL_SERVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 2000, "예기치 못한 오류가 발생했습니다."),
    NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 2001, "존재하지 않는 리소스입니다."),
    INVALID_VALUE_EXCEPTION(HttpStatus.BAD_REQUEST, 2002, "올바르지 않은 요청 값입니다."),
    UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, 2003, "권한이 없는 요청입니다."),
    ALREADY_DELETE_EXCEPTION(HttpStatus.BAD_REQUEST, 2004, "이미 삭제된 리소스입니다."),
    FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, 2005, "인가되지 않는 요청입니다."),
    ALREADY_EXIST_EXCEPTION(HttpStatus.BAD_REQUEST, 2006, "이미 존재하는 리소스입니다."),


    // 3000: Auth Error
    KAKAO_TOKEN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 3000, "토큰 발급에서 오류가 발생했습니다."),
    KAKAO_USER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 3001, "카카오 프로필 정보를 가져오는 과정에서 오류가 발생했습니디."),
    WRONG_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, 3002, "유효하지 않은 토큰입니다."),
    LOGOUT_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, 3003, "로그아웃된 토큰입니다"),
    WRONG_TOKEN(HttpStatus.UNAUTHORIZED, 3004, "유효하지 않은 토큰입니다."),


    //4000: Apply Error
    NOT_APPLY_EXCEPTION(HttpStatus.BAD_REQUEST,4000,"지원 기간 지났습니다"),
    NOT_FOUND_PLACE_EXCEPTION(HttpStatus.NOT_FOUND,4001,"존재하지 않는 여행지입니다."),
    NOT_FOUND_PLAN_EXCEPTION(HttpStatus.NOT_FOUND,4001,"존재하지 않는 일정입니다."),
    NOT_FOUND_APPLY_EXCEPTION(HttpStatus.NOT_FOUND,4002,"존재하지 않는 지원서입니다."),
    ALREADY_APPLY_EXCEPTION(HttpStatus.BAD_REQUEST,4003,"이미 지원했습니다."),
    ALREADY_DECISION_EXCEPION(HttpStatus.BAD_REQUEST,4004,"이미 지원 결정했습니다."),
    NOT_RECRUITING_EXCEPION(HttpStatus.BAD_REQUEST,4005,"이 공고는 모집 중이 아닙니다."),
    NOT_FOUND_CATEGORY_EXCEPTION(HttpStatus.NOT_FOUND,4006,"카테고리가 없습니다"),
    OVER_APPLY_EXCEPTION(HttpStatus.NOT_FOUND,4007,"지원 파트 정원이 찼습니다."),
    INVALID_MAP_EXCEPTION(HttpStatus.NOT_FOUND,4008,"모든 좌표값이 요청되지 않았습니다."),
    NOT_FOUND_PLACE_REVIEW_EXCEPTION(HttpStatus.NOT_FOUND,4009,"존재하지 않는 여행지리뷰입니다."),

    //5000: Post Error
    NOT_POST_EXCEPTION(HttpStatus.BAD_REQUEST,5000,"공고를 더 이상 생성할 수 없습니다"),
    POST_VALUE_EXCEPTION(HttpStatus.BAD_REQUEST,5001,"올바르지 않은 요청 값입니다."),
    NOT_FOUNT_SCRAP_EXCEPTION(HttpStatus.NOT_FOUND,5002,"스크랩 정보가 존재하지 않습니다."),
    ALREADY_FINISH_EXCEPTION(HttpStatus.BAD_REQUEST, 5003, "이미 모집 기간이 마감된 공고입니다."),
    ILLEGAL_POST_EXCEPTION(HttpStatus.BAD_REQUEST, 5004, "파트별 인원수가 전체 인원수와 일치하지 않습니다."),
    NOT_ADD_IMAGE_EXCEPTION(HttpStatus.BAD_REQUEST,5005,"예상치 못한 에러가 발생하여 여행지 후기 이미지 추가가 되지않습니다.");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
