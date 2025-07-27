package Journey.Together.domain.plan.factory;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.plan.dto.PlanReq;
import Journey.Together.domain.plan.entity.Plan;
import org.springframework.stereotype.Component;

@Component
public class PlanFactory {
    public Plan createPlan(Member member, PlanReq planReq) {
        return Plan.builder()
                .member(member)
                .title(planReq.title())
                .startDate(planReq.startDate())
                .endDate(planReq.endDate())
                .isPublic(false)
                .build();
    }
}
