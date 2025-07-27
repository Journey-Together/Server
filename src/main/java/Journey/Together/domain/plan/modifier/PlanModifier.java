package Journey.Together.domain.plan.modifier;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.plan.dto.PlanReq;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.repository.DayRepository;
import Journey.Together.domain.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanModifier {
    private final DayRepository dayRepository;
    private final PlanRepository planRepository;

    public void modifyPlan(Member member, Plan plan, PlanReq planReq) {
        // 기존 Day 삭제
        dayRepository.deleteAllByMemberAndPlan(member, plan);

        // Plan 수정
        plan.updatePlan(planReq.title(), planReq.startDate(), planReq.endDate());

        // save는 변경 감지 안 될 경우에만 필요
        planRepository.save(plan);
    }
}
