package Journey.Together.domain.place.repository;

import Journey.Together.domain.place.entity.DisabilityPlaceCategory;
import Journey.Together.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisabilityPlaceCategoryRepository extends JpaRepository<DisabilityPlaceCategory, Long> {

    List<DisabilityPlaceCategory> findAllByPlace(Place place);
}
