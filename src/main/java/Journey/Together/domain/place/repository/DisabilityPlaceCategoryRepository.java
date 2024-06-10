package Journey.Together.domain.place.repository;

import Journey.Together.domain.place.entity.DisabilityPlaceCategory;
import Journey.Together.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DisabilityPlaceCategoryRepository extends JpaRepository<DisabilityPlaceCategory, Long> {

    List<DisabilityPlaceCategory> findAllByPlace(Place place);


    @Query("SELECT DISTINCT dpc.subCategory.category.id FROM DisabilityPlaceCategory dpc " +
            "WHERE dpc.place.id = :placeId")
    List<Long> findDisabilityCategoryIds(@Param("placeId") long placeId);

    @Query("SELECT DISTINCT dpc.subCategory.subname FROM DisabilityPlaceCategory dpc " +
            "WHERE dpc.place.id = :placeId")
    List<String> findDisabilitySubCategoryNames(@Param("placeId") long placeId);
}
