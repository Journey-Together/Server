package Journey.Together.domain.dairy.service;

import Journey.Together.domain.dairy.dto.*;
import Journey.Together.domain.dairy.entity.Day;
import Journey.Together.domain.dairy.entity.Plan;
import Journey.Together.domain.dairy.entity.PlanReview;
import Journey.Together.domain.dairy.entity.PlanReviewImage;
import Journey.Together.domain.dairy.repository.DayRepository;
import Journey.Together.domain.dairy.repository.PlanRepository;
import Journey.Together.domain.dairy.repository.PlanReviewImageRepository;
import Journey.Together.domain.dairy.repository.PlanReviewRepository;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.repository.MemberRepository;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.PlaceRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanService {
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final DayRepository dayRepository;
    private final PlaceRepository placeRepository;
    private final PlanReviewRepository planReviewRepository;
    private final PlanReviewImageRepository planReviewImageRepository;
    private final S3Client s3Client;

    @Transactional
    public void savePlan(Member member, PlanReq planReq){
        // Validation
        memberRepository.findMemberByEmailAndDeletedAtIsNull(member.getEmail()).orElseThrow(()->new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION));
        //Buisness
        Plan plan = Plan.builder()
                .member(member)
                .title(planReq.title())
                .startDate(planReq.startDate())
                .endDate(planReq.endDate())
                .build();
        planRepository.save(plan);
        //날짜별 장소 정보 저장
        for(DailyPlace dailyPlace : planReq.dailyplace()){
            Place place = placeRepository.findPlaceById(dailyPlace.placeId());
            if(place == null){
                throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
            }
            Day day = Day.builder()
                    .member(member)
                    .plan(plan)
                    .place(place)
                    .date(dailyPlace.date())
                    .build();
            dayRepository.save(day);
        }
    }
    @Transactional
    public void updatePlan(Member member,Long planId){
        // Validation
        Plan plan = planRepository.findPlanByMemberAndPlanIdAndDeletedAtIsNull(member,planId);
        if(plan == null){
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }

    }
    @Transactional
    public void deletePlan(Member member,Long planId){
        // Validation
        Plan plan = planRepository.findPlanByMemberAndPlanIdAndDeletedAtIsNull(member,planId);
        if(plan == null){
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }
        //Buisness
        dayRepository.deleteAllByMemberAndPlan(member,plan);
        planRepository.deletePlanByPlanId(planId);

    }

    @Transactional
    public void savePlanReview(Member member, Long planId, PlanReviewReq planReviewReq,List<MultipartFile> images){
        // Validation
        Plan plan = planRepository.findPlanByMemberAndPlanIdAndEndDateIsBeforeAndDeletedAtIsNull(member,planId,LocalDate.now());
        if(plan == null){
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }
        //Business
        PlanReview planReview = PlanReview.builder()
                .grade(planReviewReq.grade())
                .content(planReviewReq.content())
                .plan(plan)
                .build();
        planReviewRepository.save(planReview);

        List<PlanReviewImage> list = new ArrayList<>();
        for(MultipartFile file : images){
            String uuid = UUID.randomUUID().toString();
            String url = s3Client.upload(file,member.getProfileUuid(),uuid);
            PlanReviewImage planReviewImage = PlanReviewImage.builder()
                    .planReview(planReview)
                    .imageUrl(url)
                    .build();
            planReviewImageRepository.save(planReviewImage);
            list.add(planReviewImage);
        }
        planReview.setPlanReviewImages(list);

        plan.setIsPublic(planReviewReq.isPublic());
    }

    @Transactional
    public List<MyPlanRes> findMyPlans(Member member) {
        //Vaildation
        List<Plan> list = planRepository.findAllByMemberAndDeletedAtIsNull(member);
        if(list == null){
            return null;
        }
        //Business
        List<MyPlanRes> myPlanResList = new ArrayList<>();
        for(Plan plan : list){
            List<Day> dayList = dayRepository.findByMemberAndDateAndPlanOrderByCreatedAtDesc(member,plan.getStartDate(),plan);
            String image = dayList.get(0).getPlace().getFirstImg();
            if (LocalDate.now().isAfter(plan.getEndDate())){
                Boolean hasReview = planReviewRepository.existsAllByPlan(plan);
                MyPlanRes myPlanRes = MyPlanRes.of(plan,image,null,hasReview);
                myPlanResList.add(myPlanRes);
            }else if ((LocalDate.now().isEqual(plan.getStartDate()) || LocalDate.now().isAfter(plan.getStartDate())) && (LocalDate.now().isEqual(plan.getEndDate()) || LocalDate.now().isBefore(plan.getEndDate()))){
                MyPlanRes myPlanRes = MyPlanRes.of(plan,image,"D-DAY",null);
                myPlanResList.add(myPlanRes);
            }else if (LocalDate.now().isBefore(plan.getStartDate())){
                Period period = Period.between(LocalDate.now(),plan.getStartDate());
                MyPlanRes myPlanRes = MyPlanRes.of(plan,image,"D-"+ period.getDays(),null);
                myPlanResList.add(myPlanRes);
            }
        }

        //Response
        return myPlanResList;
    }

    @Transactional
    public PlaceInfoPageRes searchPlace(String word, Pageable page){
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("createdAt").descending());
        Page<Place> placePage = placeRepository.findAllByNameContainsOrderByCreatedAtDesc(word,pageable);
        List<PlaceInfo> placeInfoList = placePage.getContent().stream()
                .map(PlaceInfo::of)
                .collect(Collectors.toList());
        return PlaceInfoPageRes.of(placeInfoList, placePage.getNumber(), placePage.getSize(), placePage.getTotalPages(), placePage.isLast());
    }
}
