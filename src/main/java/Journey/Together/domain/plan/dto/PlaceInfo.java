package Journey.Together.domain.plan.dto;

import Journey.Together.domain.place.entity.Place;
import lombok.Builder;

@Builder
public record PlaceInfo(
        Long placeId,
        String placeName,
        String category,
        String imageUrl

) {
    public static PlaceInfo of(Place place){
        String cat;
        if(place.getCategory().equals("B02"))
            cat = "숙소";
        else if (place.getCategory().equals("A05"))
            cat = "음식점";
        else
            cat = "관광지";

        return PlaceInfo.builder()
                .placeId(place.getId())
                .placeName(place.getName())
                .imageUrl(place.getFirstImg())
                .category(cat)
                .build();
    }
}
