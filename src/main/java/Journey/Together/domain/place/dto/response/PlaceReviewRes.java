package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.Place;

import java.util.List;

public record PlaceReviewRes(
        String placeName,
        String placeAddress,
        String placeImg,
        Integer pageNo,
        Integer pageSize,
        Integer totalPages,
        List<PlaceReivewListDto> myplaceReviewList
) {
    static public PlaceReviewRes of(Place place, List<PlaceReivewListDto> myplaceReviewList, Integer pageNo,
                                    Integer pageSize,
                                    Integer totalPages){
        return new PlaceReviewRes(place.getName(),  place.getAddress(),place.getFirstImg(),pageNo,pageSize, totalPages ,myplaceReviewList);
    }
}
