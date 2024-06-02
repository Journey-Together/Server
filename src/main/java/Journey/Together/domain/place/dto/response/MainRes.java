package Journey.Together.domain.place.dto.response;

import java.util.List;

public record MainRes(
        List<PlaceRes> recommendPlaceList,
        List<PlaceRes> aroundPlaceList
) {
}
