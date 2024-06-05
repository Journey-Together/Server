package Journey.Together.domain.dairy.dto;

import Journey.Together.domain.place.dto.response.PlaceDetailRes;
import Journey.Together.domain.place.dto.response.PlaceReviewDto;
import Journey.Together.domain.place.entity.Place;
import lombok.Builder;

import java.util.List;

@Builder
public record PlaceInfo(
        Long placeId,
        String placeName,
        String category,
        String imageUrl

) {
    static String cat = "관광지";
    public static PlaceInfo of(Place place){
        if(place.getCategory().equals("B02"))
            cat = "숙소";
        else if (place.getCategory().equals("A05"))
            cat = "음식점";
        return PlaceInfo.builder()
                .placeId(place.getId())
                .placeName(place.getName())
                .imageUrl(place.getFirstImg())
                .category(cat)
                .build();
    }
}
