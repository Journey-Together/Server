package Journey.Together.domain.place.dto.response;

import java.util.List;

public record SearchPlaceRes(
        Long size,
        List<PlaceRes> placeResList
) {
}
