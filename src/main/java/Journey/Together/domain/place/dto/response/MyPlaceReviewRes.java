package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.PlaceReview;

public record MyPlaceReviewRes(
        Long reviewId,
        String imgae,
        String address,
        String name,
        Float grade
) {
    public static MyPlaceReviewRes of(PlaceReview placeReview, String firstImg){
        return new MyPlaceReviewRes(placeReview.getId(), firstImg, placeReview.getPlace().getAddress(), placeReview.getPlace().getName(), placeReview.getGrade());
    }
}
