package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.bookbark.entity.PlaceBookmark;

public record PlaceBookmarkDto(
        Long placeId,
        String placeName
) {

   public static PlaceBookmarkDto of(PlaceBookmark placeBookmark){
       return new PlaceBookmarkDto(placeBookmark.getPlace().getId(), placeBookmark.getPlace().getName());
   }
}
