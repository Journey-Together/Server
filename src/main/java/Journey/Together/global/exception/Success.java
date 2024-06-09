package Journey.Together.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Success {

	/**
	 * 201 CREATED
	 */
	CREATE_PLAN_SUCCESS(HttpStatus.CREATED, "일정 저장이 완료 되었습니다."),
	CREATE_CATEGORY_SUCCESS(HttpStatus.CREATED, "새 카테고리 추가 성공"),
	CREATE_REVIEW_SUCCESS(HttpStatus.CREATED, "리뷰 생성 성공"),
	CREATE_PLACE_REVIEW_SUCCESS(HttpStatus.CREATED, "리뷰 작성 성공"),

	/**
	 * 200 OK
	 */

	GET_PLACE_REVIEW_LIST_SUCCESS(HttpStatus.OK, "관광정보 후기 목록 조회 성공"),
	GET_MY_PLACE_REVIEW_LIST_SUCCESS(HttpStatus.OK, "나의 여행지 후기 목록 조회 성공"),
	GET_MY_PLACE_REVIEW_SUCCESS(HttpStatus.OK, "나의 여행지 후기 조회 성공"),
	GET_BOOKMARK_PLACES_SUCCESS(HttpStatus.OK, "여행지 북마크 조회 성공"),
	GET_BOOKMARK_PLAN_SUCCESS(HttpStatus.OK, "일정 북마크 조회 성공"),
	GET_BOOKMARK_PLACE_NAMES_SUCCESS(HttpStatus.OK, "북마크한 여행지 조회 성공"),
	GET_USER_INTEREST_SUCCESS(HttpStatus.OK, "사용자 관심 유형 정보 조회 성공"),
	GET_MAIN_SUCCESS(HttpStatus.OK, "메인 페이지 정보 조회 성공"),
	GET_PLACE_DETAIL_SUCCESS(HttpStatus.OK, "여행지 상세정보 조회 성공"),
	GET_MYPAGE_SUCCESS(HttpStatus.OK, "마이 페이지 조회 성공"),
	GET_MYPLAN_SUCCESS(HttpStatus.OK, "내 일정 조회 성공"),
	GET_PLAN_SUCCESS(HttpStatus.OK, "내 일정 조회 성공"),
	GET_SITES_SUCCESS(HttpStatus.OK, "추천 사이트 조회 성공"),
	GET_SETTINGS_SUCCESS(HttpStatus.OK, "설정 페이지 조회 성공"),

	GET_CATEORIES_SUCCESS(HttpStatus.OK, "전체 카테고리 조회 성공"),
	GET_CATEORY_SUCCESS(HttpStatus.OK, "세부 카테고리 조회 성공"),
	GET_TIMER_SUCCESS(HttpStatus.OK, "타이머 조회 성공"),
	GET_TIMER_PAGE_SUCCESS(HttpStatus.OK, "타이머 페이지 조회 성공"),
	GET_DUPLICATED_SUCCESS(HttpStatus.OK, "중복 여부 체크 성공"),

	LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공"),
	RE_ISSUE_TOKEN_SUCCESS(HttpStatus.OK, "토큰 재발급 성공"),
	SIGNOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공"),
	DELETE_USER_SUCCESS(HttpStatus.OK, "회원 탈퇴가 정상적으로 이루어졌습니다."),
	DELETE_PLAN_SUCCESS(HttpStatus.OK, "일정 삭제 성공"),
	DELETE_CATEGORY_SUCCESS(HttpStatus.OK, "카테고리 삭제 성공"),
	DELETE_MY_PLACE_REVIEW_SUCCESS(HttpStatus.OK, "나의 여행지 후기 삭제 성공"),
	UPDATE_MY_PLACE_REVIEW_SUCCESS(HttpStatus.OK, "나의 여행지 후기 수정 성공"),
	SEARCH_SUCCESS(HttpStatus.OK, "검색 성공"),
	PARSING_OG_SUCCESS(HttpStatus.OK, "og 데이터 파싱 결과입니다. 크롤링을 막은 페이지는 기본이미지가 나옵니다."),
	UPDATE_PUSH_ALLOWED_SUCCESS(HttpStatus.OK, "푸시알림 수정 성공"),
	UPDATE_PLAN_SUCCESS(HttpStatus.CREATED, "일정 수정이 완료 되었습니다."),

	UPDATE_ISREAD_SUCCESS(HttpStatus.OK, "열람여부 수정 완료"),
	UPDATE_CATEGORY_TITLE_SUCCESS(HttpStatus.OK, "카테고리 수정 완료"),
	UPDATE_TIMER_DATETIME_SUCCESS(HttpStatus.OK, "타이머 시간/날짜 수정 완료"),
	UPDATE_TIMER_COMMENT_SUCCESS(HttpStatus.OK, "타이머 코멘트 수정 완료"),
	CHANGE_BOOKMARK_SUCCESS(HttpStatus.OK, "북마크 상태 수정 완료"),
	PUSH_ALARM_PERIODIC_SUCCESS(HttpStatus.OK, "푸시알림 활성에 성공했습니다."),
	PUSH_ALARM_SUCCESS(HttpStatus.OK, "푸시알림 전송에 성공했습니다."),
	CLEAR_SCHEDULED_TASKS_SUCCESS(HttpStatus.OK, "스케줄러에서 예약된 작업을 제거했습니다."),

	UPDATE_USER_INFO_SUCCESS(HttpStatus.OK, "사용자 정보 수정 완료"),


	/**
	 * 204 NO_CONTENT
	 */
	SEARCH_SUCCESS_BUT_IS_EMPTY(HttpStatus.NO_CONTENT, "검색에 성공했지만 조회된 내용이 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	public int getHttpStatusCode(){
		return httpStatus.value();
	}

}
