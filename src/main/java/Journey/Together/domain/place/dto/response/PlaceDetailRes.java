package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.Place;

import java.util.List;

public record PlaceDetailRes(
        Long placeId,
        String name,
        String imgae,
        String address,
        String category,
        String overview,
        String mapX,
        String mapY,

        Boolean isMark,

        Integer bookmarkNum,

        List<Long> disability,
        List<Long> subDisability,
        List<PlaceReviewDto> reviewList
) {
    static String cat = "관광지";
    public static PlaceDetailRes of(Place place, Boolean isMark, Integer bookmarkNum, List<Long> disability, List<Long> subDisability, List<PlaceReviewDto> reviewList){
        if(place.getCategory().equals("B02"))
            cat = "숙소";
        else if (place.getCategory().equals("A05"))
            cat = "맛집";



        return new PlaceDetailRes(place.getId(), place.getName(), place.getFirstImg(), place.getAddress(), cat, place.getOverview(), place.getMapX(), place.getMapY(), isMark,
                bookmarkNum, disability, subDisability, reviewList);
    }
}
