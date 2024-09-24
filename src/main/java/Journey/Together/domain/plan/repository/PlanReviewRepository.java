package Journey.Together.domain.plan.repository;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.entity.PlanReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PlanReviewRepository extends JpaRepository<PlanReview,Long> {
    boolean existsAllByPlanAndDeletedAtIsNull(Plan plan);
    @Query("SELECT EXISTS (" +
            "SELECT 1 FROM PlanReview pr " +
            "WHERE pr.plan = :plan AND (pr.report IS NULL OR pr.report = false))")
    boolean existsAllByPlanAndReportFilter(@Param("plan") Plan plan);

    @Query("SELECT pr FROM PlanReview pr WHERE pr.plan = :plan")
    PlanReview findPlanReview(Plan plan);

    PlanReview findPlanReviewByPlanReviewId(Long id);
    PlanReview findPlanReviewByPlanAndDeletedAtIsNull(Plan plan);
    @Query("SELECT COUNT(pr) FROM PlanReview pr WHERE pr.member = :member")
    Long countPlanReviewByMember(@Param("member") Member member);
    PlanReview findPlanReviewByPlanReviewIdAndDeletedAtIsNull(Long reviewId);
    void deletePlanReviewByPlanReviewId(Long planReviewId);
}
