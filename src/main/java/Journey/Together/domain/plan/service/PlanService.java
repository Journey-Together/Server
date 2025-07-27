package Journey.Together.domain.plan.service;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.validator.MemberValidator;
import Journey.Together.domain.plan.dto.PlanDetailRes;
import Journey.Together.domain.plan.dto.PlanReq;
import Journey.Together.domain.plan.dto.PlanRes;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.repository.PlanRepository;
import Journey.Together.domain.plan.service.deleter.PlanDeleter;
import Journey.Together.domain.plan.service.factory.PlanFactory;
import Journey.Together.domain.plan.service.modifier.PlanModifier;
import Journey.Together.domain.plan.service.query.PlanDetailQueryService;
import Journey.Together.domain.plan.service.query.PlanQueryService;
import Journey.Together.domain.plan.service.validator.PlanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;

    private final PlanPlaceService planPlaceService;
    private final PlanQueryService planQueryService;
    private final PlanDetailQueryService planDetailQueryService;

    private final PlanFactory planFactory;
    private final PlanModifier planModifier;
    private final PlanDeleter planDeleter;

    private final PlanValidator planValidator;
    private final MemberValidator memberValidator;

    @Transactional
    public void savePlan(Member member, PlanReq planReq) {
        // Validation
        memberValidator.validateExistsAndActive(member.getEmail());
        //Buisness
        Plan plan = planFactory.createPlan(member, planReq);
        planRepository.save(plan);
        //날짜별 장소 정보 저장
        planPlaceService.savePlacesByDay(planReq.dailyplace(), member, plan);
    }

    @Transactional
    public void updatePlan(Member member, Long planId, PlanReq planReq) {
        // Validation
        Plan plan = planRepository.findPlanByMemberAndPlanIdAndDeletedAtIsNull(member, planId);
        planValidator.validateExists(plan);
        //Business
        planModifier.modifyPlan(member, plan, planReq);

        planPlaceService.savePlacesByDay(planReq.dailyplace(), member, plan);
    }

    @Transactional
    public PlanRes findPlan(Member member, Long planId) {
        // Validation
        Plan plan = planRepository.findPlanByMemberAndPlanIdAndDeletedAtIsNull(member, planId);
        planValidator.validateExists(plan);
        //Buisness
        String image = planQueryService.getFirstPlaceImageOfPlan(plan);
        //Response
        return PlanRes.of(plan, image, null, null);
    }

    @Transactional
    public void deletePlan(Member member, Long planId) {
        // Validation
        Plan plan = planRepository.findPlanByMemberAndPlanIdAndDeletedAtIsNull(member, planId);
        planValidator.validateExists(plan);

        planDeleter.delete(member, plan);
    }

    @Transactional
    public PlanDetailRes findPlanDetail(Member member, Long planId) {
        // Validation
        Plan plan = planRepository.findPlanByPlanIdAndDeletedAtIsNull(planId);
        planValidator.validateExists(plan);

        return planDetailQueryService.getDetail(member, plan);
    }

    @Transactional
    public Boolean updatePlanIsPublic(Member member, Long planId) {
        // Validation
        Plan plan = planRepository.findPlanByMemberAndPlanIdAndEndDateIsBeforeAndDeletedAtIsNull(member, planId, LocalDate.now());
        planValidator.validateExists(plan);
        planValidator.validateWriter(member, plan);

        //Response
        return planModifier.togglePublic(plan);
    }
}
