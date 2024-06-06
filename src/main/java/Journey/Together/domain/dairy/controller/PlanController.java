package Journey.Together.domain.dairy.controller;

import Journey.Together.domain.dairy.dto.*;
import Journey.Together.domain.dairy.service.PlanService;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ApiResponse updatePlan(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("plan_id") Long planId){
        planService.updatePlan(principalDetails.getMember(),planId);
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

    @GetMapping("/search")
    public ApiResponse<PlaceInfoPageRes> searchPlace(@RequestParam String word, @PageableDefault(size = 6,page = 0) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planService.searchPlace(word,pageable));
    }

    @PostMapping("/review/{plan_id}")
    public ApiResponse savePlanReview(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("plan_id")Long planId, @RequestPart(required = false) List<MultipartFile> images, @RequestPart PlanReviewReq planReviewReq){
        planService.savePlanReview(principalDetails.getMember(),planId,planReviewReq,images);
        return ApiResponse.success(Success.CREATE_REVIEW_SUCCESS);
    }

    @GetMapping("/open")
    public ApiResponse<OpenPlanPageRes> findOpenPlans(@PageableDefault(size = 6) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planService.findOpenPlans(pageable));
    }

    @GetMapping("/my")
    public ApiResponse<List<MyPlanRes>> findMyPlans(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(Success.GET_MYPLAN_SUCCESS,planService.findMyPlans(principalDetails.getMember()));
    }

    @GetMapping("/my/not-complete")
    public ApiResponse<PlanPageRes> findNotComplete(@AuthenticationPrincipal PrincipalDetails principalDetails,@PageableDefault(size = 6,page = 0) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planService.findNotComplete(principalDetails.getMember(),pageable));
    }
    @GetMapping("/my/complete")
    public ApiResponse<PlanPageRes> findComplete(@AuthenticationPrincipal PrincipalDetails principalDetails,@PageableDefault(size = 6) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planService.findComplete(principalDetails.getMember(),pageable));
    }

}
