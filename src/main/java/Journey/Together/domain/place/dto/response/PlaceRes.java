package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.Place;
import com.querydsl.core.annotations.QueryProjection;

import java.nio.DoubleBuffer;
import java.util.List;

public record PlaceRes(
        Long placeId,
        String name,
        String image,
        List<Long> disability,
        String address,
        String mapX,
        String mapY
) {
    public static PlaceRes of(Place place, List<Long> disability){

        return new PlaceRes(place.getId(), place.getName(), place.getFirstImg(), disability,place.getAddress(), place.getMapX().toString(), place.getMapY().toString());
    }
}
