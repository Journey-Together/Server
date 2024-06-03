package Journey.Together.domain.dairy.controller;

import Journey.Together.domain.dairy.dto.PlanReq;
import Journey.Together.domain.dairy.service.PlanService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plan")
@Tag(name = "Plan", description = "일정 관련 API")
public class PlanController {
    private final PlanService planService;
    @PostMapping("")
    public ApiResponse savePlan(@AuthenticationPrincipal PrincipalDetails principalDetails, PlanReq planReq){
        planService.savePlan(principalDetails.getMember(),planReq);
        return ApiResponse.success(Success.CREATE_PLAN_SUCCESS);
    }
}
