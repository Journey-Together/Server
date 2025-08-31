package Journey.Together.global.security.kakao.dto;

import lombok.Builder;

@Builder
public record KakaoToken(
        String token_type,
        String access_token,
        String id_token,
        Integer expires_in,
        String refresh_token,
        Integer refresh_token_expires_in

) {
}
