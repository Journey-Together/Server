package Journey.Together.domain.place.repository;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.entity.PlaceReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview,Long> {

    PlaceReview findPlaceReviewByPlace(Place place);

    Page<PlaceReview> findAllByPlaceOrderByCreatedAtDesc(Place place, Pageable pageable);

    Page<PlaceReview> findAllByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);

}
