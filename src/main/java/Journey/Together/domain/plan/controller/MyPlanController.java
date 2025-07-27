package Journey.Together.domain.plan.controller;

import Journey.Together.domain.plan.dto.MyPlanRes;
import Journey.Together.domain.plan.dto.PlanPageRes;
import Journey.Together.domain.plan.service.query.PlanQueryService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plan/my")
@Tag(name = "MyPlan", description = "내 일정 관련 API")
public class MyPlanController {
    private final PlanQueryService planQueryService;

    @GetMapping("")
    public ApiResponse<List<MyPlanRes>> findMyPlans(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(Success.GET_MYPLAN_SUCCESS,planQueryService.findMyPlans(principalDetails.getMember()));
    }

    @GetMapping("/not-complete")
    public ApiResponse<PlanPageRes> findNotComplete(@AuthenticationPrincipal PrincipalDetails principalDetails,@PageableDefault(size = 6,page = 0) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planQueryService.findIsCompelete(principalDetails.getMember(),pageable,false));
    }
    @GetMapping("/complete")
    public ApiResponse<PlanPageRes> findComplete(@AuthenticationPrincipal PrincipalDetails principalDetails,@PageableDefault(size = 6) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planQueryService.findIsCompelete(principalDetails.getMember(),pageable,true));
    }

}
