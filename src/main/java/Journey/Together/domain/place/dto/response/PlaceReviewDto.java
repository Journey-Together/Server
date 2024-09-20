package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.PlaceReview;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PlaceReviewDto(
        Long reviewId,
        String nickname,
        String profileImg,
        String content,
        List<String> reviewImgs,
        Float grade,
        LocalDate date,
        Boolean myReview
) {

    public static PlaceReviewDto of(PlaceReview placeReview, String profileImg, List<String> reviewImgs, Boolean myReview){
        return new PlaceReviewDto(placeReview.getId(), placeReview.getMember().getNickname(), profileImg,
                placeReview.getContent(), reviewImgs, placeReview.getGrade(), placeReview.getDate(),myReview );
    }
}
