package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.Place;

import java.util.List;

public record SearchPlace(
        List<Place> places,
        Long size
) {
}
