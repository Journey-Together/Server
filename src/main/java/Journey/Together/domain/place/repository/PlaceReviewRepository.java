package Journey.Together.domain.place.repository;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.entity.PlaceReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview,Long> {

    PlaceReview findPlaceReviewById(Long id);

    List<PlaceReview> findTop2ByPlaceAndReportIsNullOrReportFalseOrderByCreatedAtDesc(Place place);
    PlaceReview findPlaceReviewByMemberAndPlace(Member member, Place place);
    @Query("SELECT COUNT(pr) FROM PlaceReview pr WHERE pr.member = :member")
    Long countPlaceReviewByMember(@Param("member") Member member);

    Page<PlaceReview> findAllByPlaceAndReportIsNullOrReportFalseOrderByCreatedAtDesc(Place place, Pageable pageable);

    Page<PlaceReview> findAllByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);

}
