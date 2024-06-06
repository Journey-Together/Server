package Journey.Together.domain.placeReview.repository;

import Journey.Together.domain.placeReview.entity.PlaceReview;
import Journey.Together.domain.placeReview.entity.PlaceReviewImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceReviewImgRepository extends JpaRepository<PlaceReviewImg,Long> {

    List<PlaceReviewImg> findAllByPlaceReview(PlaceReview placeReview);
}
