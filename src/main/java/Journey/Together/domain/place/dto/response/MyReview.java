package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.PlaceReview;

import java.time.LocalDate;
import java.util.List;

public record MyReview(
        Long reviewId,
        List<String> images,
        String placeName,
        String address,
        Float grade,
        LocalDate date,
        String content
) {
    static public MyReview of(PlaceReview placeReview, List<String> images){
        return new MyReview(placeReview.getId(), images, placeReview.getPlace().getAddress(), placeReview.getPlace().getName(),placeReview.getGrade(), placeReview.getDate(), placeReview.getContent());
    }
}
