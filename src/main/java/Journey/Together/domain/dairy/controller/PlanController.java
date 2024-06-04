package Journey.Together.domain.dairy.controller;

import Journey.Together.domain.dairy.dto.PlanReq;
import Journey.Together.domain.dairy.service.PlanService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plan")
@Tag(name = "Plan", description = "일정 관련 API")
public class PlanController {
    private final PlanService planService;
    @PostMapping("")
    public ApiResponse savePlan(@AuthenticationPrincipal PrincipalDetails principalDetails,@RequestBody PlanReq planReq){
        planService.savePlan(principalDetails.getMember(),planReq);
        return ApiResponse.success(Success.CREATE_PLAN_SUCCESS);
    }

    @PostMapping("/{plan_id}")
    public ApiResponse savePlan(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("plan_id") Long planId){
        planService.updatePlan(principalDetails.getMember(),planId);
        return ApiResponse.success(Success.UPDATE_PLAN_SUCCESS);
    }

    @DeleteMapping("/{plan_id}")
    public ApiResponse deletePlan(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("plan_id") Long planId){
        planService.deletePlan(principalDetails.getMember(),planId);
        return ApiResponse.success(Success.DELETE_PLAN_SUCCESS);
    }

}
