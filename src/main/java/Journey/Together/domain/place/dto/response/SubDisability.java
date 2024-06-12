package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.DisabilityPlaceCategory;

public record SubDisability(
        String subDisabilityName,
        String description
) {
    public static SubDisability of(DisabilityPlaceCategory disabilityPlaceCategory){
        return new SubDisability(disabilityPlaceCategory.getSubCategory().getSubname(), disabilityPlaceCategory.getDescription());
    }
}
