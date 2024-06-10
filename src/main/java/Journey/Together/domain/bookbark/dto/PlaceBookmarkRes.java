package Journey.Together.domain.bookbark.dto;

import Journey.Together.domain.bookbark.entity.PlaceBookmark;
import Journey.Together.domain.place.entity.Place;

import java.util.List;

public record PlaceBookmarkRes(
        Long placeId,
        String name,
        String image,
        String address,
        List<Long> disability
) {
    public static PlaceBookmarkRes of (Place place, List<Long> disability){
        return new PlaceBookmarkRes(place.getId(), place.getName(), place.getFirstImg(), place.getAddress(), disability);
    }
}
