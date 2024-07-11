package Journey.Together.domain.place.dto.response;

import java.util.List;

public record SearchPlaceRes(
        List<PlaceRes> placeResList,

        Integer pageNo,
        Integer pageSize,
        Long totalSize
) {
}
