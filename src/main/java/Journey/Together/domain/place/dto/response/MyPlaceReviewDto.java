package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.PlaceReview;

public record MyPlaceReviewDto(
        Long reviewId,
        String imgae,
        String address,
        String name,
        Float grade
) {
    public static MyPlaceReviewDto of(PlaceReview placeReview, String firstImg){
        return new MyPlaceReviewDto(placeReview.getId(), firstImg, placeReview.getPlace().getAddress(), placeReview.getPlace().getName(), placeReview.getGrade());
    }
}
