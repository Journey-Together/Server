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

    @Query("SELECT pr FROM PlaceReview pr WHERE pr.place = :place AND (pr.report IS NULL OR pr.report = false) ORDER BY pr.createdAt DESC")
    List<PlaceReview> findTop2ByPlaceAndReportIsNullOrReportFalseOrderByCreatedAtDesc(@Param("place") Place place, Pageable pageable);

    PlaceReview findPlaceReviewByMemberAndPlace(Member member, Place place);
    @Query("SELECT COUNT(pr) FROM PlaceReview pr WHERE pr.member = :member")
    Long countPlaceReviewByMember(@Param("member") Member member);

    @Query("SELECT COUNT(pr)  FROM PlaceReview pr WHERE pr.place = :place AND (pr.report IS NULL OR pr.report = false) ORDER BY pr.createdAt DESC")
    Long countPlaceReviewByPlaceAndReportIsNullOrReportFalseOrderByCreatedAtDesc(@Param("place") Place place);

    @Query("SELECT pr FROM PlaceReview pr WHERE pr.place = :place AND (pr.report IS NULL OR pr.report = false) ORDER BY pr.createdAt DESC")
    Page<PlaceReview> findAllByPlaceAndReportIsNullOrReportFalseOrderByCreatedAtDesc(Place place, Pageable pageable);

    Page<PlaceReview> findAllByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);

}
