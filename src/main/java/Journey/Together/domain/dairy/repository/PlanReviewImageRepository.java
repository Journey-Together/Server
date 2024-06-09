package Journey.Together.domain.dairy.repository;

import Journey.Together.domain.dairy.entity.PlanReview;
import Journey.Together.domain.dairy.entity.PlanReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanReviewImageRepository extends JpaRepository<PlanReviewImage,Long> {
    List<PlanReviewImage> findAllByPlanReviewAndDeletedAtIsNull(PlanReview planReview);
    void deletePlanReviewImageByPlanReviewImageId(Long planReviewImageId);
}
