package Journey.Together.domain.plan.service.deleter;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.entity.PlanReview;
import Journey.Together.domain.plan.repository.DayRepository;
import Journey.Together.domain.plan.repository.PlanRepository;
import Journey.Together.domain.plan.repository.PlanReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanDeleter {

    private final DayRepository dayRepository;
    private final PlanReviewRepository planReviewRepository;
    private final PlanRepository planRepository;

    public void delete(Member member, Plan plan) {
        dayRepository.deleteAllByMemberAndPlan(member, plan);

        PlanReview planReview = planReviewRepository.findPlanReviewByPlanAndDeletedAtIsNull(plan);
        if (planReview != null) {
            planReviewRepository.delete(planReview);
        }

        planRepository.deletePlanByPlanId(plan.getPlanId());
    }
}
