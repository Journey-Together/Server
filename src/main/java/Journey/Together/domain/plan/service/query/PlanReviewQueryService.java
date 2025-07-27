package Journey.Together.domain.plan.service.query;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.plan.dto.PlanReviewRes;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.entity.PlanReview;
import Journey.Together.domain.plan.repository.PlanRepository;
import Journey.Together.domain.plan.repository.PlanReviewRepository;
import Journey.Together.domain.plan.service.PlanReviewImageService;
import Journey.Together.domain.plan.service.validator.PlanValidator;
import Journey.Together.global.util.S3UrlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanReviewQueryService {

    private final PlanRepository planRepository;
    private final PlanReviewRepository planReviewRepository;
    private final PlanValidator planValidator;
    private final PlanReviewImageService planReviewImageService;
    private final S3UrlUtil s3UrlUtil;

    public PlanReviewRes getReview(Member member, long planId) {
        Plan plan = planRepository.findPlanByPlanIdAndDeletedAtIsNull(planId);
        planValidator.validateExists(plan);

        PlanReview planReview = planReviewRepository.findPlanReview(plan);
        boolean isWriter = (member != null) && plan.getMember().getMemberId().equals(member.getMemberId());

        String profileUrl = s3UrlUtil.generateProfileUrl(plan.getMember().getProfileUuid());

        if (planReview == null) {
            return PlanReviewRes.of(null, null, null, isWriter, false, null, profileUrl, null);
        }

        List<String> imageList = planReviewImageService.getImageUrls(planReview);
        if (planReview.getReport() == null) {
            return PlanReviewRes.of(planReview.getPlanReviewId(), planReview.getContent(), planReview.getGrade(),
                    isWriter, true, imageList, profileUrl, null);
        }

        return PlanReviewRes.of(planReview.getPlanReviewId(), planReview.getContent(), planReview.getGrade(),
                isWriter, true, imageList, profileUrl, planReview.getReport());
    }
}


