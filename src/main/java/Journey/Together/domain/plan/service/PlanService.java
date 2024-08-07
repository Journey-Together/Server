package Journey.Together.domain.plan.service;

import Journey.Together.domain.place.entity.PlaceReviewImg;
import Journey.Together.domain.plan.dto.*;
import Journey.Together.domain.plan.entity.Day;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.entity.PlanReview;
import Journey.Together.domain.plan.entity.PlanReviewImage;
import Journey.Together.domain.plan.repository.DayRepository;
import Journey.Together.domain.plan.repository.PlanRepository;
import Journey.Together.domain.plan.repository.PlanReviewImageRepository;
import Journey.Together.domain.plan.repository.PlanReviewRepository;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.repository.MemberRepository;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.DisabilityPlaceCategoryRepository;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.util.S3Client;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
    private final DisabilityPlaceCategoryRepository disabilityPlaceCategoryRepository;
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
                .isPublic(false)
                .build();
        planRepository.save(plan);
        //날짜별 장소 정보 저장
        for(DailyPlace dailyPlace : planReq.dailyplace()){
            for(Long placeId : dailyPlace.places()){
                Place place = placeRepository.findPlaceById(placeId);
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
    }
    @Transactional
    public void updatePlan(Member member,Long planId,PlanReq planReq){
        // Validation
        Plan plan = planRepository.findPlanByMemberAndPlanIdAndDeletedAtIsNull(member,planId);
        if(plan == null){
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }
        //Business
        //날짜별 장소 삭제
        dayRepository.deleteAllByMemberAndPlan(member,plan);
        //일정 update
        plan.setTitle(planReq.title());
        plan.setStartDate(planReq.startDate());
        plan.setEndDate(planReq.endDate());
        planRepository.save(plan);
        //날짜별 장소 정보 저장
        for(DailyPlace dailyPlace : planReq.dailyplace()){
            for(Long placeId : dailyPlace.places()){
                Place place = placeRepository.findPlaceById(placeId);
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

    }
    @Transactional
    public PlanRes findPlan(Member member,Long planId) {
        // Validation
        Plan plan = planRepository.findPlanByMemberAndPlanIdAndDeletedAtIsNull(member, planId);
        if (plan == null) {
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }
        //Buisness
        String image = getPlaceFirstImage(plan);
        //Response
        return PlanRes.of(plan,image,null,null);
    }
    @Transactional
    public void deletePlan(Member member,Long planId){
        // Validation
        Plan plan = planRepository.findPlanByMemberAndPlanIdAndDeletedAtIsNull(member,planId);
        if(plan == null){
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }
        PlanReview planReview = planReviewRepository.findPlanReviewByPlanAndDeletedAtIsNull(plan);
        //Buisness
        dayRepository.deleteAllByMemberAndPlan(member,plan);
        if(planReview!=null){
            deletePlanReview(member,planReview.getPlanReviewId());
        }
        planRepository.deletePlanByPlanId(planId);

    }

    @Transactional
    public PlanDetailRes findPlanDetail(Member member, Long planId){
        // Validation
        Plan plan = planRepository.findPlanByPlanIdAndDeletedAtIsNull(planId);
        if(plan == null){
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }
        //Buisness
        boolean isWriter;
        List<DailyList> dailyLists = new ArrayList<>();
        List<Day> dayList = dayRepository.findAllByMemberAndPlanOrderByDateAsc(plan.getMember(),plan);
        List<String> imageUrls = getPlaceImageList(plan);

        Map<LocalDate, List<Day>> groupedByDate = dayList.stream()
                .collect(Collectors.groupingBy(Day::getDate));

        LocalDate startDate = plan.getStartDate();
        LocalDate endDate = plan.getEndDate();

// startDate부터 endDate까지의 날짜들을 순회
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<Day> days = groupedByDate.get(date);
            List<DailyPlaceInfo> dailyPlaceInfoList = new ArrayList<>();

            if (days != null) {
                for (Day day : days) {
                    List<Long> disabilityCategoryList = disabilityPlaceCategoryRepository.findDisabilityCategoryIds(day.getPlace().getId());
                    DailyPlaceInfo dailyPlaceInfo = DailyPlaceInfo.of(day.getPlace(), disabilityCategoryList);
                    dailyPlaceInfoList.add(dailyPlaceInfo);
                }
            }

            DailyList dailyList = DailyList.of(date, dailyPlaceInfoList);
            dailyLists.add(dailyList);
        }

// 날짜 순으로 정렬
        dailyLists.sort(Comparator.comparing(DailyList::getDate));

//        Map<LocalDate, List<Day>> groupedByDate = dayList.stream()
//                .collect(Collectors.groupingBy(Day::getDate));
//        groupedByDate.entrySet().stream()
//                .sorted(Map.Entry.comparingByKey()) // 날짜 순으로 정렬
//                .forEach(entry -> {
//                    LocalDate date = entry.getKey();
//                    List<Day> days = entry.getValue();
//
//                    List<DailyPlaceInfo> dailyPlaceInfoList = new ArrayList<>();
//                    for (Day day : days) {
//                        List<Long> disabilityCategoryList = disabilityPlaceCategoryRepository.findDisabilityCategoryIds(day.getPlace().getId());
//                        DailyPlaceInfo dailyPlaceInfo = DailyPlaceInfo.of(day.getPlace(), disabilityCategoryList);
//                        dailyPlaceInfoList.add(dailyPlaceInfo);
//                    }
//                    DailyList dailyList = DailyList.of(date, dailyPlaceInfoList);
//                    dailyLists.add(dailyList);
//                });
        if (member ==null){
            isWriter = false;
        }else {
            isWriter = plan.getMember().getMemberId().equals(member.getMemberId());
        }

        String remainDate = null;
        if ((LocalDate.now().isEqual(plan.getStartDate()) || LocalDate.now().isAfter(plan.getStartDate())) && (LocalDate.now().isEqual(plan.getEndDate()) || LocalDate.now().isBefore(plan.getEndDate()))){
            remainDate = "D-Day";
        }else if (LocalDate.now().isBefore(plan.getStartDate())){
            Period period = Period.between(LocalDate.now(),plan.getStartDate());
            remainDate = "D-"+ period.getDays();
        }

        //PlanDetailRes - List<String> imageUrls, List<DailyList> dailyList, Boolean isWriter
        //Response
        return PlanDetailRes.of(imageUrls,dailyLists,isWriter,plan,remainDate);
    }

    @Transactional
    public Boolean updatePlanIsPublic(Member member,Long planId){
        // Validation
        Plan plan = planRepository.findPlanByMemberAndPlanIdAndEndDateIsBeforeAndDeletedAtIsNull(member,planId,LocalDate.now());
        if(plan == null){
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }
        if(!Objects.equals(plan.getMember().getMemberId(), member.getMemberId())){
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        //Business
        plan.setIsPublic(!plan.getIsPublic());

        //Response
        return plan.getIsPublic();
    }

    @Transactional
    public void savePlanReview(Member member, Long planId, PlanReviewReq planReviewReq,List<MultipartFile> images){
        // Validation
        Plan plan = planRepository.findPlanByMemberAndPlanIdAndEndDateIsBeforeAndDeletedAtIsNull(member,planId,LocalDate.now());
        if(plan == null){
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }
        if(planReviewRepository.existsAllByPlan(plan)){
            throw new ApplicationException(ErrorCode.ALREADY_EXIST_EXCEPTION);
        }
        //Business
        PlanReview planReview = PlanReview.builder()
                .member(member)
                .grade(planReviewReq.grade())
                .content(planReviewReq.content())
                .plan(plan)
                .build();
        planReviewRepository.save(planReview);

        if(images!=null){
            for(MultipartFile file : images){
                String uuid = UUID.randomUUID().toString();
                String url = s3Client.upload(file,member.getProfileUuid(),uuid);
                PlanReviewImage planReviewImage = PlanReviewImage.builder()
                        .planReview(planReview)
                        .imageUrl(url)
                        .build();
                planReviewImageRepository.save(planReviewImage);
            }
        }

        plan.setIsPublic(planReviewReq.isPublic());
    }

    @Transactional
    public PlanReviewRes findPlanReview(Member member,long planId){
        // Validation
        Plan plan = planRepository.findPlanByPlanIdAndDeletedAtIsNull(planId);
        if(plan == null){
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }
        PlanReview planReview = planReviewRepository.findPlanReviewByPlan(plan);
        //Buisness
        boolean isWriter;
        if (member ==null){
            isWriter = false;
        }else {
            isWriter = plan.getMember().getMemberId().equals(member.getMemberId());
        }
        List<String> imageList = getReviewImageList(plan);
        String profileUrl = s3Client.baseUrl()+plan.getMember().getProfileUuid()+"/profile";
        if(planReview==null){
            return PlanReviewRes.of(null,null,null,isWriter,false,imageList,profileUrl);
        }else {
            return PlanReviewRes.of(planReview.getPlanReviewId(),planReview.getContent(),planReview.getGrade(),isWriter,true,imageList,profileUrl);
        }

    }

    @Transactional
    public void updatePlanReview(Member member, Long reviewId, UpdatePlanReviewReq updatePlanReviewReq, List<MultipartFile> images) {
        // Validation
        PlanReview planReview = planReviewRepository.findPlanReviewByPlanReviewIdAndDeletedAtIsNull(reviewId);
        if (planReview.getPlan().getMember().getMemberId() != member.getMemberId()) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
        //Business
        if (images != null) {
            try {
                for(MultipartFile file : images) {
                    String uuid = UUID.randomUUID().toString();
                    String url = s3Client.upload(file,member.getProfileUuid(),uuid);
                    PlanReviewImage planReviewImage = PlanReviewImage.builder()
                            .planReview(planReview)
                            .imageUrl(url)
                            .build();
                    planReviewImageRepository.save(planReviewImage);
                }
            } catch (RuntimeException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        if (updatePlanReviewReq != null) {
            if (updatePlanReviewReq.grade() != null) {
                planReview.setGrade(updatePlanReviewReq.grade());
            }
            if (updatePlanReviewReq.content() != null) {
                planReview.setContent(updatePlanReviewReq.content());
            }
            if (updatePlanReviewReq.deleteImgUrls() != null) {
                updatePlanReviewReq.deleteImgUrls().forEach(
                        deleteImg -> {
                            planReviewImageRepository.deletePlanReviewImageByImageUrl(deleteImg);
                            s3Client.delete(StringUtils.substringAfter(deleteImg, "com/"));
                        }
                );
            }
        }

    }

    @Transactional
    public void deletePlanReview(Member member,Long reviewId){
        //Vailda
        PlanReview planReview = planReviewRepository.findPlanReviewByPlanReviewIdAndDeletedAtIsNull(reviewId);
        if(planReview.getPlan().getMember().getMemberId()!=member.getMemberId()){
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
        List<PlanReviewImage> planReviewImageList = planReviewImageRepository.findAllByPlanReviewAndDeletedAtIsNull(planReview);
        if(planReviewImageList!=null){
            for(PlanReviewImage planReviewImage : planReviewImageList){
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
        if(list == null){
            return null;
        }
        //Business
        List<Plan> top3list = list.stream()
                .sorted(Comparator.comparingLong(plan -> Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), plan.getStartDate()))))
                .limit(3)
                .toList();
        List<MyPlanRes> myPlanResList = new ArrayList<>();
        for(Plan plan : top3list){
            String image = getPlaceFirstImage(plan);
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
        return PlaceInfoPageRes.of(placeInfoList, placePage.getNumber(), placePage.getSize(), placePage.getTotalPages(), placePage.isLast(),placePage.getTotalElements());
    }

    @Transactional
    public PlanPageRes findNotComplete(Member member,Pageable page){
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("createdAt").descending());
        Page<Plan> planPage = planRepository.findAllByMemberAndEndDateGreaterThanEqualAndDeletedAtIsNull(member,LocalDate.now(),pageable);
        List<PlanRes> planResList = planPage.getContent().stream()
                .map(plan -> PlanRes.of(plan,getPlaceFirstImage(plan),isBetween(plan.getStartDate(),plan.getEndDate()),null))
                .collect(Collectors.toList());
        return PlanPageRes.of(planResList,planPage.getNumber(),planPage.getSize(),planPage.getTotalPages(),planPage.isLast());
    }

    @Transactional
    public PlanPageRes findComplete(Member member, Pageable page){
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("createdAt").descending());
        Page<Plan> planPage = planRepository.findAllByMemberAndEndDateBeforeAndDeletedAtIsNull(member,LocalDate.now(),pageable);
        List<PlanRes> planResList = planPage.getContent().stream()
                .map(plan -> PlanRes.of(plan,getPlaceFirstImage(plan),null,planReviewRepository.existsAllByPlan(plan)))
                .collect(Collectors.toList());
        return PlanPageRes.of(planResList,planPage.getNumber(),planPage.getSize(),planPage.getTotalPages(),planPage.isLast());
    }

    @Transactional
    public OpenPlanPageRes findOpenPlans(Pageable page){
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("createdAt").descending());
        Page<Plan> planPage = planRepository.findAllByEndDateBeforeAndIsPublicIsTrueAndDeletedAtIsNull(LocalDate.now(),pageable);
        List<OpenPlanRes> openPlanResList = planPage.getContent().stream()
                .map(plan -> OpenPlanRes.of(plan, s3Client.baseUrl()+plan.getMember().getProfileUuid()+"/profile",getPlaceFirstImage(plan)))
                .collect(Collectors.toList());
        return OpenPlanPageRes.of(openPlanResList,planPage.getNumber(),planPage.getSize(),planPage.getTotalPages(),planPage.isLast());
    }

    public String isBetween(LocalDate startDate,LocalDate endDate){
       if ((LocalDate.now().isEqual(startDate) || LocalDate.now().isAfter(startDate) && (LocalDate.now().isEqual(endDate) || LocalDate.now().isBefore(endDate)))){
            return "D-DAY";
       }else if (LocalDate.now().isBefore(startDate)){
            Period period = Period.between(LocalDate.now(),startDate);
            return "D-"+ period.getDays();
       }
       return null;
    }

//    public String getPlanImageUrl(Member member,Plan plan){
//        //Buisness
//        //다가오는 일정-> 첫번째날 첫번째 장소 사진(1장) (없을경우 null로 처리)
//        if(plan.getEndDate().isAfter(LocalDate.now())){
//            return getPlaceFirstImage(member,plan);
//        }
//        //다녀온 일정
//        else {
//            PlanReview planReview = planReviewRepository.findPlanReviewByPlan(plan);
//            //후기가 없을 경우 -> 첫번째날 첫번째 장소 사진(1장)
//            if(planReview==null){
//                return getPlaceFirstImage(member,plan);
//            }
//            //후기가 있을 경우
//            else {
//                //후기가 있지만 후기 사진이 없을 경우 -> 첫번째날 첫번째 장소 사진(1장)
//                List<PlanReviewImage> planReviewImageList = planReviewImageRepository.findAllByPlanReviewAndDeletedAtIsNull(planReview);
//                if(planReviewImageList == null || planReviewImageList.isEmpty()){
//                    return getPlaceFirstImage(member,plan);
//                }
//                //다녀온 일정 (후기 사진 있을 경우) -> 후기 사진들 (여러장)
//                else {
//                    return planReviewImageList.get(0).getImageUrl();
//                }
//            }
//        }
//    }

    public String getPlaceFirstImage(Plan plan){
        List<Day> dayList = dayRepository.findByPlanOrderByCreatedAtDesc(plan);
        if(!dayList.isEmpty()){
            String placeImageUrl = dayList.get(0).getPlace().getFirstImg();
            if(placeImageUrl.isEmpty()){
                return null;
            }
            return dayList.get(0).getPlace().getFirstImg();
        }
        return null;
    }

    public List<String> getPlaceImageList(Plan plan){
        List<Day> dayList = dayRepository.findByPlanOrderByCreatedAtDesc(plan);
        List<String> list = new ArrayList<>();
        if(!dayList.isEmpty()){
           dayList.forEach(day ->{list.add(day.getPlace().getFirstImg());});
           return list;
        }
        return null;
    }

    public List<String> getReviewImageList(Plan plan){
        List<String> list = new ArrayList<>();
        PlanReview planReview = planReviewRepository.findPlanReviewByPlan(plan);
        List<PlanReviewImage> planReviewImageList = planReviewImageRepository.findAllByPlanReviewAndDeletedAtIsNull(planReview);
        for(PlanReviewImage planReviewImage : planReviewImageList){
            list.add(planReviewImage.getImageUrl());
        }
        return list;
    }
}
