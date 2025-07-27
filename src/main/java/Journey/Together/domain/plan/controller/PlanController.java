package Journey.Together.domain.plan.controller;

import Journey.Together.domain.plan.dto.OpenPlanPageRes;
import Journey.Together.domain.plan.dto.PlanDetailRes;
import Journey.Together.domain.plan.dto.PlanReq;
import Journey.Together.domain.plan.dto.PlanRes;
import Journey.Together.domain.plan.service.PlanService;
import Journey.Together.domain.plan.service.query.PlanQueryService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plan")
@Tag(name = "Plan", description = "일정 관련 API")
public class PlanController {
    private final PlanService planService;
    private final PlanQueryService planQueryService;

    @PostMapping("")
    public ApiResponse savePlan(@AuthenticationPrincipal PrincipalDetails principalDetails,@RequestBody PlanReq planReq){
        planService.savePlan(principalDetails.getMember(),planReq);
        return ApiResponse.success(Success.CREATE_PLAN_SUCCESS);
    }

    @PatchMapping("/{plan_id}")
    public ApiResponse updatePlan(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("plan_id") Long planId, @RequestBody PlanReq planReq){
        planService.updatePlan(principalDetails.getMember(),planId,planReq);
        return ApiResponse.success(Success.UPDATE_PLAN_SUCCESS);
    }

    @GetMapping("/{plan_id}")
    public ApiResponse<PlanRes> findPlan(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("plan_id") Long planId){
        return ApiResponse.success(Success.GET_PLAN_SUCCESS,planService.findPlan(principalDetails.getMember(),planId));
    }

    @DeleteMapping("/{plan_id}")
    public ApiResponse deletePlan(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("plan_id") Long planId){
        planService.deletePlan(principalDetails.getMember(),planId);
        return ApiResponse.success(Success.DELETE_PLAN_SUCCESS);
    }

    @PatchMapping("/{plan_id}/public")
    public ApiResponse updatePlanIsPublic(@AuthenticationPrincipal PrincipalDetails principalDetails,@PathVariable("plan_id") Long planId){
        return ApiResponse.success(Success.UPDATE_PLAN_SUCCESS,planService.updatePlanIsPublic(principalDetails.getMember(),planId));
    }

    @GetMapping("/open")
    public ApiResponse<OpenPlanPageRes> findOpenPlans(@PageableDefault(size = 6) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planQueryService.findOpenPlans(pageable));
    }

    @GetMapping("/detail/{plan_id}")
    public ApiResponse<PlanDetailRes> findPalnDetailInfo(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("plan_id")Long planId){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planService.findPlanDetail(principalDetails.getMember(),planId));
    }

    @GetMapping("/guest/detail/{plan_id}")
    public ApiResponse<PlanDetailRes> findPalnDetailInfo(@PathVariable("plan_id")Long planId){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planService.findPlanDetail(null,planId));
    }
}
