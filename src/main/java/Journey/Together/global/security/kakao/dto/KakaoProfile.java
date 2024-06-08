package Journey.Together.global.security.kakao.dto;

public record KakaoProfile(
        // 2023년 12월까지 없었던 것으로 보이는 데이터인데, 현재 계속 조회됨. (포럼에 문의된 상황)
        Boolean setPrivacyInfo,
        Long id,
        String connected_at,
        Properties properties,
        KakaoAccount kakao_account
) {
    // 계정 프로퍼티 내용 (카카오 문서에 맞추어 Snake Case 사용)
    public record Properties(
            String nickname,
            String profile_image,
            String thumbnail_image,
            String name
    ) {
    }

    // 사용자의 카카오 계정 정보
    public record KakaoAccount(
            Boolean profile_nickname_needs_agreement,
            Boolean profile_image_needs_agreement,
            Boolean name_needs_agreement,
            Profile profile,
            Boolean has_email,
            Boolean email_needs_agreement,
            Boolean is_email_valid,
            Boolean is_email_verified,
            String email,
            String name
    ) {
        public record Profile(
                String nickname,
                String thumbnail_image_url,
                String profile_image_url,
                Boolean is_default_image,
                Boolean is_default_nickname
        ) {
        }

    }
}
