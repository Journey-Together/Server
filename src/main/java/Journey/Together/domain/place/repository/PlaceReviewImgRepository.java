package Journey.Together.domain.place.repository;

import Journey.Together.domain.place.entity.PlaceReview;
import Journey.Together.domain.place.entity.PlaceReviewImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceReviewImgRepository extends JpaRepository<PlaceReviewImg,Long> {

    List<PlaceReviewImg> findAllByPlaceReview(PlaceReview placeReview);

}
