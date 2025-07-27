package Journey.Together.domain.plan.service;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.plan.dto.PlanReviewReq;
import Journey.Together.domain.plan.dto.UpdatePlanReviewReq;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.entity.PlanReview;
import Journey.Together.domain.plan.repository.PlanRepository;
import Journey.Together.domain.plan.repository.PlanReviewRepository;
import Journey.Together.domain.plan.service.factory.PlanReviewFactory;
import Journey.Together.domain.plan.service.modifier.PlanModifier;
import Journey.Together.domain.plan.service.modifier.PlanReviewModifier;
import Journey.Together.domain.plan.service.validator.PlanReviewValidator;
import Journey.Together.domain.plan.service.validator.PlanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanReviewService {
    private final PlanRepository planRepository;
    private final PlanReviewRepository planReviewRepository;
    private final PlanReviewImageService planReviewImageService;
    private final PlanReviewModifier planReviewModifier;
    private final PlanModifier planModifier;
    private final PlanReviewValidator planReviewValidator;
    private final PlanValidator planValidator;
    private final PlanReviewFactory planReviewFactory;

    @Transactional
    public void savePlanReview(Member member, Long planId, PlanReviewReq planReviewReq, List<MultipartFile> images) {
        // Validation
        Plan plan = planRepository.findPlanByMemberAndPlanIdAndEndDateIsBeforeAndDeletedAtIsNull(member, planId, LocalDate.now());
        planValidator.validateExists(plan);
        planReviewValidator.validateExists(plan);

        //Business
        PlanReview planReview = planReviewFactory.createPlanReview(member, plan, planReviewReq);
        planReviewRepository.save(planReview);

        if (images != null) {
            planReviewImageService.uploadAndSaveImages(images, planReview, member.getProfileUuid());
        }

        planModifier.updateIsPublic(plan, planReviewReq.isPublic());
    }

    @Transactional
    public void updatePlanReview(Member member, Long reviewId, UpdatePlanReviewReq req, List<MultipartFile> images) {
        PlanReview planReview = planReviewRepository.findPlanReviewByPlanReviewIdAndDeletedAtIsNull(reviewId);
        planReviewValidator.validateWriter(member, planReview);

        // 이미지 업로드
        if (images != null && !images.isEmpty()) {
            planReviewImageService.uploadAndSaveImages(images, planReview, member.getProfileUuid());
        }

        // 이미지 삭제
        if (req.deleteImgUrls() != null) {
            planReviewImageService.deleteImages(req.deleteImgUrls());
        }

        // 내용 수정
        planReviewModifier.update(planReview, req);
    }
}
