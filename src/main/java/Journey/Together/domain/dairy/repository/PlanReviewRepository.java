package Journey.Together.domain.dairy.repository;

import Journey.Together.domain.dairy.entity.Plan;
import Journey.Together.domain.dairy.entity.PlanReview;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanReviewRepository extends JpaRepository<PlanReview,Long> {
    boolean existsAllByPlan(Plan plan);
    PlanReview findPlanReviewByPlan(Plan plan);
}
