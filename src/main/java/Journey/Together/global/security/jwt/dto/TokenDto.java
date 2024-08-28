package Journey.Together.global.security.jwt.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public record TokenDto(
        String accessToken,
        String refreshToken
) {
}
