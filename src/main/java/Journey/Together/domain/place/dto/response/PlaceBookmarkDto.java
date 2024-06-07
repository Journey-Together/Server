package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.placeBookbark.entity.PlaceBookmark;

public record PlaceBookmarkDto(
        Long placeId,
        String placeName
) {

   public static PlaceBookmarkDto of(PlaceBookmark placeBookmark){
       return new PlaceBookmarkDto(placeBookmark.getPlace().getId(), placeBookmark.getPlace().getName());
   }
}
