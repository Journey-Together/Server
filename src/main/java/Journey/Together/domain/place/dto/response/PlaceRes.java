package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.Place;

import java.util.List;

public record PlaceRes(
        Long placeId,
        String name,
        String image,
        List<String> disability,
        String address
) {
    public static PlaceRes of(Place place, List<String> disability){

        return new PlaceRes(place.getId(), place.getName(), place.getFirstImg(), disability,place.getAddress());
    }
}
