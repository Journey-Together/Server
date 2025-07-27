package Journey.Together.domain.plan.service.factory;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.plan.dto.PlanReviewReq;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.entity.PlanReview;
import org.springframework.stereotype.Component;

@Component
public class PlanReviewFactory {
    public PlanReview createPlanReview(Member member, Plan plan, PlanReviewReq planReviewReq) {
        return PlanReview.builder()
                .member(member)
                .grade(planReviewReq.grade())
                .content(planReviewReq.content())
                .plan(plan)
                .build();
    }
}
