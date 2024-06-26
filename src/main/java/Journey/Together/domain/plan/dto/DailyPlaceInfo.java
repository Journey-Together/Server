package Journey.Together.domain.plan.dto;

import Journey.Together.domain.place.entity.Place;
import lombok.Builder;

import java.util.List;

@Builder
public record DailyPlaceInfo(
        Long placeId,
        String name,
        String category,
        List<Long> disabilityCategoryList
) {
    static String cat = "관광지";
    public static DailyPlaceInfo of( Place place,
                                     List<Long> disabilityCategoryList){
        if(place.getCategory().equals("B02"))
            cat = "숙소";
        else if (place.getCategory().equals("A05"))
            cat = "음식점";
        return DailyPlaceInfo.builder()
                .placeId(place.getId())
                .name(place.getName())
                .category(cat)
                .disabilityCategoryList(disabilityCategoryList)
                .build();
    }
}
