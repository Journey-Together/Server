package Journey.Together.domain.plan.service.factory;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.plan.dto.PlanReq;
import Journey.Together.domain.plan.entity.Day;
import Journey.Together.domain.plan.entity.Plan;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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

    public Day createDay(Member member, Plan plan, Place place, LocalDate date) {
        return Day.builder()
                .member(member)
                .plan(plan)
                .place(place)
                .date(date)
                .build();
    }
}
