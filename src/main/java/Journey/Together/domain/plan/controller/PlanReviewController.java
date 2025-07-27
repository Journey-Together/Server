package Journey.Together.domain.plan.controller;

import Journey.Together.domain.plan.dto.PlanReviewReq;
import Journey.Together.domain.plan.dto.PlanReviewRes;
import Journey.Together.domain.plan.dto.UpdatePlanReviewReq;
import Journey.Together.domain.plan.service.PlanReviewService;
import Journey.Together.domain.plan.service.query.PlanReviewQueryService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plan")
@Tag(name = "Plan", description = "일정 관련 API")
public class PlanReviewController {
    private final PlanReviewQueryService planReviewQueryService;
    private final PlanReviewService planReviewService;

    @PostMapping("{plan_id}/reviews")
    public ApiResponse savePlanReview(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("plan_id")Long planId, @RequestPart(required = false) List<MultipartFile> images, @RequestPart PlanReviewReq planReviewReq){
        planReviewService.savePlanReview(principalDetails.getMember(),planId,planReviewReq,images);
        return ApiResponse.success(Success.CREATE_REVIEW_SUCCESS);
    }

    @GetMapping("/review/{plan_id}")
    public ApiResponse<PlanReviewRes> findPlanReview(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("plan_id")Long planId){
        return ApiResponse.success(Success.GET_REVIEW_SUCCESS,planReviewQueryService.getReview(principalDetails.getMember(),planId));
    }

    @GetMapping("/guest/review/{plan_id}")
    public ApiResponse<PlanReviewRes> findPlanReviewGuest(@PathVariable("plan_id")Long planId){
        return ApiResponse.success(Success.GET_REVIEW_SUCCESS,planReviewQueryService.getReview(null,planId));
    }

    @PatchMapping("/reviews/{review_id}")
    public ApiResponse updatePlanReview(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("review_id")Long reviewId, @RequestPart(required = false) List<MultipartFile> images, @RequestPart(required = false) UpdatePlanReviewReq planReviewReq){
        planReviewService.updatePlanReview(principalDetails.getMember(),reviewId,planReviewReq,images);
        return ApiResponse.success(Success.UPDATE_REVIEW_SUCCESS);
    }

    @DeleteMapping("/reviews/{review_id}")
    public ApiResponse deletePlanReview(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("review_id")Long reviewId){
        planReviewService.deletePlanReview(principalDetails.getMember(),reviewId);
        return ApiResponse.success(Success.DELETE_PLAN_REVIEW_SUCCESS);
    }
}
