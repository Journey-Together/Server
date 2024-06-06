package Journey.Together.domain.placeReview.repository;

import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.placeReview.entity.PlaceReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview,Long> {

    PlaceReview findPlaceReviewByPlace(Place place);

}
