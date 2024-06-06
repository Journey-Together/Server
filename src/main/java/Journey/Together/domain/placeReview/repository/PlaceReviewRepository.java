package Journey.Together.domain.placeReview.repository;

import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.placeReview.entity.PlaceReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview,Long> {

    PlaceReview findPlaceReviewByPlace(Place place);

    Page<PlaceReview> findAllByPlaceOrderByCreatedAtDesc(Place place, Pageable pageable);

}
