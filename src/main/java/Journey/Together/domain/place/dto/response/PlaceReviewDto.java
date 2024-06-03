package Journey.Together.domain.place.dto.response;

import java.time.LocalDateTime;

public record PlaceReviewDto(
        Long reviewId,
        String nickname,
        String profileImg,
        String content,
        String reviewImg,
        Float grade,
        LocalDateTime date) {
}
