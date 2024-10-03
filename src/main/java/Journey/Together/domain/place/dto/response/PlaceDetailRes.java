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
        Boolean isReview,

        String tel,
        String homepage,

        List<Long> disability,
        List<SubDisability> subDisability,
        List<PlaceReviewDto> reviewList
) {
    static String cat = "관광지";
    public static PlaceDetailRes of(Place place, Boolean isMark, Integer bookmarkNum, List<Long> disability, List<SubDisability> subDisability, List<PlaceReviewDto> reviewList, Boolean isReview){
        if(place.getCategory().equals("B02"))
            cat = "숙소";
        else if (place.getCategory().equals("A05"))
            cat = "맛집";

        else
            cat = "관광지";

        String place_tel=null;
        String place_homepage=null;

        if(place.getId() != null && !place.getTel().isBlank())
            place_tel = place.getTel();

        if(place.getHomepage() != null && !place.getHomepage().isBlank())
            place_homepage = place.getHomepage();


        return new PlaceDetailRes(place.getId(), place.getName(), place.getFirstImg(), place.getAddress(), cat, place.getOverview(), place.getMapX().toString(), place.getMapY().toString(), isMark,
                bookmarkNum, isReview, place_tel,place_homepage,disability, subDisability, reviewList);
    }
}
