package Journey.Together.domain.plan.service;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.validator.MemberValidator;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.domain.plan.dto.*;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.entity.PlanReview;
import Journey.Together.domain.plan.entity.PlanReviewImage;
import Journey.Together.domain.plan.repository.PlanRepository;
import Journey.Together.domain.plan.repository.PlanReviewImageRepository;
import Journey.Together.domain.plan.repository.PlanReviewRepository;
import Journey.Together.domain.plan.service.deleter.PlanDeleter;
import Journey.Together.domain.plan.service.factory.PlanFactory;
import Journey.Together.domain.plan.service.factory.PlanReviewFactory;
import Journey.Together.domain.plan.service.modifier.PlanModifier;
import Journey.Together.domain.plan.service.query.PlanDetailQueryService;
import Journey.Together.domain.plan.service.query.PlanQueryService;
import Journey.Together.domain.plan.service.validator.PlanReviewValidator;
import Journey.Together.domain.plan.service.validator.PlanValidator;
import Journey.Together.domain.plan.util.DateUtil;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.util.S3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final PlaceRepository placeRepository;
    private final PlanReviewRepository planReviewRepository;
    private final PlanReviewImageRepository planReviewImageRepository;

    private final PlanPlaceService planPlaceService;
    private final PlanReviewImageService planReviewImageService;

    private final PlanQueryService planQueryService;
    private final PlanDetailQueryService planDetailQueryService;

    private final PlanFactory planFactory;
    private final PlanReviewFactory planReviewFactory;
    private final PlanModifier planModifier;
    private final PlanDeleter planDeleter;

    private final PlanValidator planValidator;
    private final PlanReviewValidator planReviewValidator;
    private final MemberValidator memberValidator;

    private final S3Client s3Client;


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

    @Transactional
    public void deletePlanReview(Member member, Long reviewId) {
        //Vailda
        PlanReview planReview = planReviewRepository.findPlanReviewByPlanReviewIdAndDeletedAtIsNull(reviewId);
        if (!Objects.equals(planReview.getPlan().getMember().getMemberId(), member.getMemberId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
        List<PlanReviewImage> planReviewImageList = planReviewImageRepository.findAllByPlanReviewAndDeletedAtIsNull(planReview);
        if (planReviewImageList != null) {
            for (PlanReviewImage planReviewImage : planReviewImageList) {
                String filename = planReviewImage.getImageUrl().replace(s3Client.baseUrl(), "");
                s3Client.delete(filename);
                planReviewImageRepository.deletePlanReviewImageByPlanReviewImageId(planReviewImage.getPlanReviewImageId());
            }
        }
        planReviewRepository.deletePlanReviewByPlanReviewId(planReview.getPlanReviewId());
    }

    @Transactional
    public List<MyPlanRes> findMyPlans(Member member) {
        //Vaildation
        List<Plan> list = planRepository.findAllByMemberAndDeletedAtIsNull(member);
        //Business
        List<Plan> top3list = list.stream()
                .sorted(Comparator.comparingLong(plan -> Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), plan.getStartDate()))))
                .limit(3)
                .toList();
        List<MyPlanRes> myPlanResList = new ArrayList<>();
        for (Plan plan : top3list) {
            String image = planQueryService.getFirstPlaceImageOfPlan(plan);
            String remainDate = null;
            Boolean hasReview = null;
            if (LocalDate.now().isAfter(plan.getEndDate())) {
                hasReview = planReviewRepository.existsAllByPlanAndDeletedAtIsNull(plan);
            } else if ((LocalDate.now().isEqual(plan.getStartDate()) || LocalDate.now().isAfter(plan.getStartDate())) && (LocalDate.now().isEqual(plan.getEndDate()) || LocalDate.now().isBefore(plan.getEndDate()))) {
                remainDate = "D-DAY";
            } else if (LocalDate.now().isBefore(plan.getStartDate())) {
                Period period = Period.between(LocalDate.now(), plan.getStartDate());
                remainDate = "D-" + period.getDays();
            }
            MyPlanRes myPlanRes = MyPlanRes.of(plan, image, remainDate, hasReview);
            myPlanResList.add(myPlanRes);
        }

        //Response
        return myPlanResList;
    }

    @Transactional
    public PlaceInfoPageRes searchPlace(String word, Pageable page) {
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("createdAt").descending());
        Page<Place> placePage = placeRepository.findAllByNameContainsOrderByCreatedAtDesc(word, pageable);
        List<PlaceInfo> placeInfoList = placePage.getContent().stream()
                .map(PlaceInfo::of)
                .collect(Collectors.toList());
        return PlaceInfoPageRes.of(placeInfoList, placePage.getNumber(), placePage.getSize(), placePage.getTotalPages(), placePage.isLast(), placePage.getTotalElements());
    }

    @Transactional
    public PlanPageRes findIsCompelete(Member member, Pageable page, Boolean compelete) {
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("createdAt").descending());
        Page<Plan> planPage;
        List<PlanRes> planResList;
        if (compelete) {
            planPage = planRepository.findAllByMemberAndEndDateBeforeAndDeletedAtIsNull(member, LocalDate.now(), pageable);
            planResList = planPage.getContent().stream()
                    .map(plan -> PlanRes.of(plan, planQueryService.getFirstPlaceImageOfPlan(plan), null, planReviewRepository.existsAllByPlanAndReportFilter(plan)))
                    .collect(Collectors.toList());
        } else {
            planPage = planRepository.findAllByMemberAndEndDateGreaterThanEqualAndDeletedAtIsNull(member, LocalDate.now(), pageable);
            planResList = planPage.getContent().stream()
                    .map(plan -> PlanRes.of(plan, planQueryService.getFirstPlaceImageOfPlan(plan), DateUtil.getRemainDateString(plan.getStartDate(), plan.getEndDate()), null))
                    .collect(Collectors.toList());
        }

        return PlanPageRes.of(planResList, planPage.getNumber(), planPage.getSize(), planPage.getTotalPages(), planPage.isLast());
    }

    @Transactional
    public OpenPlanPageRes findOpenPlans(Pageable page) {
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("createdAt").descending());
        Page<Plan> planPage = planRepository.findOpenPlans(LocalDate.now(), pageable);
        List<OpenPlanRes> openPlanResList = planPage.getContent().stream()
                .map(plan -> OpenPlanRes.of(plan, s3Client.baseUrl() + plan.getMember().getProfileUuid() + "/profile_" + plan.getMember().getProfileUuid(), planQueryService.getFirstPlaceImageOfPlan(plan)))
                .collect(Collectors.toList());
        return OpenPlanPageRes.of(openPlanResList, planPage.getNumber(), planPage.getSize(), planPage.getTotalPages(), planPage.isLast());
    }
}
