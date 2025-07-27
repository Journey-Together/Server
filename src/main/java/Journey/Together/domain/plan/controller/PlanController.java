package Journey.Together.domain.plan.controller;

import Journey.Together.domain.plan.dto.*;
import Journey.Together.domain.plan.service.PlanReviewService;
import Journey.Together.domain.plan.service.PlanService;
import Journey.Together.domain.plan.service.query.PlanReviewQueryService;
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
    private final PlanReviewQueryService planReviewQueryService;
    private final PlanReviewService planReviewService;

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

    @PatchMapping("/{plan_id}/ispublic")
    public ApiResponse updatePlanIsPublic(@AuthenticationPrincipal PrincipalDetails principalDetails,@PathVariable("plan_id") Long planId){
        return ApiResponse.success(Success.UPDATE_PLAN_SUCCESS,planService.updatePlanIsPublic(principalDetails.getMember(),planId));
    }

    @GetMapping("/search")
    public ApiResponse<PlaceInfoPageRes> searchPlace(@RequestParam String word, @PageableDefault(size = 6,page = 0) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planService.searchPlace(word,pageable));
    }

    @PostMapping("/review/{plan_id}")
    public ApiResponse savePlanReview(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("plan_id")Long planId, @RequestPart(required = false) List<MultipartFile> images, @RequestPart PlanReviewReq planReviewReq){
        planReviewService.savePlanReview(principalDetails.getMember(),planId,planReviewReq,images);
        return ApiResponse.success(Success.CREATE_REVIEW_SUCCESS);
    }

    @GetMapping("/review/{plan_id}")
    public ApiResponse<PlanReviewRes> findPlanReview(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("plan_id")Long planId){
        return ApiResponse.success(Success.GET_REVIEW_SUCCESS,planReviewQueryService.getReview(principalDetails.getMember(),planId));
    }

    @PatchMapping("/review/{review_id}")
    public ApiResponse updatePlanReview(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("review_id")Long reviewId, @RequestPart(required = false) List<MultipartFile> images, @RequestPart(required = false) UpdatePlanReviewReq planReviewReq){
        planReviewService.updatePlanReview(principalDetails.getMember(),reviewId,planReviewReq,images);
        return ApiResponse.success(Success.UPDATE_REVIEW_SUCCESS);
    }

    @DeleteMapping("/review/{review_id}")
    public ApiResponse deletePlanReview(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("review_id")Long reviewId){
        planReviewService.deletePlanReview(principalDetails.getMember(),reviewId);
        return ApiResponse.success(Success.DELETE_PLAN_REVIEW_SUCCESS);
    }

    @GetMapping("/guest/review/{plan_id}")
    public ApiResponse<PlanReviewRes> findPlanReviewGuest(@PathVariable("plan_id")Long planId){
        return ApiResponse.success(Success.GET_REVIEW_SUCCESS,planReviewQueryService.getReview(null,planId));
    }

    @GetMapping("/open")
    public ApiResponse<OpenPlanPageRes> findOpenPlans(@PageableDefault(size = 6) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planService.findOpenPlans(pageable));
    }

    @GetMapping("/detail/{plan_id}")
    public ApiResponse<PlanDetailRes> findPalnDetailInfo(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("plan_id")Long planId){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planService.findPlanDetail(principalDetails.getMember(),planId));
    }

    @GetMapping("/guest/detail/{plan_id}")
    public ApiResponse<PlanDetailRes> findPalnDetailInfo(@PathVariable("plan_id")Long planId){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planService.findPlanDetail(null,planId));
    }

    @GetMapping("/my")
    public ApiResponse<List<MyPlanRes>> findMyPlans(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(Success.GET_MYPLAN_SUCCESS,planService.findMyPlans(principalDetails.getMember()));
    }

    @GetMapping("/my/not-complete")
    public ApiResponse<PlanPageRes> findNotComplete(@AuthenticationPrincipal PrincipalDetails principalDetails,@PageableDefault(size = 6,page = 0) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planService.findIsCompelete(principalDetails.getMember(),pageable,false));
    }
    @GetMapping("/my/complete")
    public ApiResponse<PlanPageRes> findComplete(@AuthenticationPrincipal PrincipalDetails principalDetails,@PageableDefault(size = 6) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_SUCCESS,planService.findIsCompelete(principalDetails.getMember(),pageable,true));
    }

}
