package Journey.Together.domain.plan.service.factory;

import Journey.Together.domain.plan.entity.PlanReview;
import Journey.Together.domain.plan.entity.PlanReviewImage;
import org.springframework.stereotype.Component;

@Component
public class PlanReviewImageFactory {
    public PlanReviewImage createPlanReviewImage(PlanReview planReview, String url) {
        return PlanReviewImage.builder()
                .planReview(planReview)
                .imageUrl(url)
                .build();
    }
}
