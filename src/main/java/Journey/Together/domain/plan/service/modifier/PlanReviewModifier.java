package Journey.Together.domain.plan.service.modifier;

import Journey.Together.domain.plan.dto.UpdatePlanReviewReq;
import Journey.Together.domain.plan.entity.PlanReview;
import org.springframework.stereotype.Component;

@Component
public class PlanReviewModifier {

    public void update(PlanReview review, UpdatePlanReviewReq req) {
        if (req.grade() != null) {
            review.setGrade(req.grade());
        }
        if (req.content() != null) {
            review.setContent(req.content());
        }
    }
}
