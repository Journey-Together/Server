package Journey.Together.domain.plan.repository;

import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.entity.PlanReview;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanReviewRepository extends JpaRepository<PlanReview,Long> {
    boolean existsAllByPlan(Plan plan);
    PlanReview findPlanReviewByPlan(Plan plan);
    PlanReview findPlanReviewByPlanReviewIdAndDeletedAtIsNull(Long reviewId);
    void deletePlanReviewByPlanReviewId(Long planReviewId);
}
