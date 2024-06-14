package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.PlaceReview;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PlaceReviewDto(
        Long reviewId,
        String nickname,
        String profileImg,
        String content,
        String reviewImg,
        Float grade,
        LocalDate date) {

    public static PlaceReviewDto of(PlaceReview placeReview, String profileImg, String img){
        return new PlaceReviewDto(placeReview.getId(), placeReview.getMember().getNickname(), profileImg,
                placeReview.getContent(), img, placeReview.getGrade(), placeReview.getDate());
    }
}
