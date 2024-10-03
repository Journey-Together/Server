package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.Place;

import java.util.List;

public record PlaceDetailGuestRes(
        Long placeId,
        String name,
        String imgae,
        String address,
        String category,
        String overview,
        String mapX,
        String mapY,
        String tel,
        String homepage,
        Integer bookmarkNum,

        List<Long> disability,
        List<SubDisability> subDisability,
        List<PlaceReviewDto> reviewList
) {
    static String cat = "관광지";
    public static PlaceDetailGuestRes of(Place place, Integer bookmarkNum, List<Long> disability, List<SubDisability> subDisability, List<PlaceReviewDto> reviewList){
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



        return new PlaceDetailGuestRes(place.getId(), place.getName(), place.getFirstImg(), place.getAddress(), cat, place.getOverview(), place.getMapX().toString(), place.getMapY().toString(),
                place_tel, place_homepage,bookmarkNum, disability, subDisability, reviewList);
    }
}
