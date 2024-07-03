package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.PlaceReview;

import java.time.LocalDate;
import java.util.List;

public record MyPlaceReviewDto(
        Long reviewId,
        Long placeId,
        String name,
        Float grade,
        LocalDate date,
        String content,
        List<String> images
) {
    public static MyPlaceReviewDto of(PlaceReview placeReview, List<String> images){
        return new MyPlaceReviewDto(placeReview.getId(),placeReview.getPlace().getId(), placeReview.getPlace().getName(), placeReview.getGrade(), placeReview.getDate(), placeReview.getContent(), images);
    }
}
