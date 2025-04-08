package Journey.Together.domain.bookbark.service;

import Journey.Together.domain.bookbark.dto.PlaceBookmarkRes;
import Journey.Together.domain.bookbark.dto.PlanBookMarkStateRes;
import Journey.Together.domain.bookbark.entity.PlaceBookmark;
import Journey.Together.domain.bookbark.entity.PlanBookmark;
import Journey.Together.domain.bookbark.entity.PlanBookmarkRes;
import Journey.Together.domain.bookbark.repository.PlaceBookmarkRepository;
import Journey.Together.domain.bookbark.repository.PlanBookmarkRepository;
import Journey.Together.domain.member.service.MemberService;
import Journey.Together.domain.plan.entity.Day;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.repository.DayRepository;
import Journey.Together.domain.plan.repository.PlanRepository;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.dto.response.PlaceBookmarkDto;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.DisabilityPlaceCategoryRepository;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.util.S3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final PlaceBookmarkRepository placeBookmarkRepository;
    private final PlanBookmarkRepository planBookmarkRepository;
    private final DisabilityPlaceCategoryRepository disabilityPlaceCategoryRepository;
    private final PlaceRepository placeRepository;

    private final DayRepository dayRepository;
    private final PlanRepository planRepository;

    private final MemberService memberService;

    private final S3Client s3Client;


    //북마크한 여행지 이름만 가져오기
    public List<PlaceBookmarkDto> getBookmarkPlaceNames(Long memberId){
        Member member = memberService.findMemberById(memberId);
        List<PlaceBookmark> placeBookmarkList = placeBookmarkRepository.findAllByMemberOrderByPlaceNameAsc(member);
        if(placeBookmarkList.isEmpty() || placeBookmarkList==null)
            return new ArrayList<>();

        return placeBookmarkList.stream().map(PlaceBookmarkDto::of).toList();
    }


    @Transactional
    // 북마크 상태변경
    public void placeBookmark(Long memberId, Long placeId){
        Place place = getPlace(placeId);
        Member member = memberService.findMemberById(memberId);

        PlaceBookmark placeBookmark = placeBookmarkRepository.findPlaceBookmarkByPlaceAndMember(place, member);
        if (placeBookmark == null) {
            PlaceBookmark newPlaceBookmark = PlaceBookmark.builder()
                    .place(place)
                    .member(member)
                    .build();
            placeBookmarkRepository.save(newPlaceBookmark);
        } else {
            // 북마크 해체
            placeBookmarkRepository.delete(placeBookmark);
        }
    }

    @Transactional
    // 북마크 상태변경
    public void planBookmark(Long memberId, Long planId){
        Member member = memberService.findMemberById(memberId);
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND_PLACE_EXCEPTION));

        PlanBookmark planBookmark = planBookmarkRepository.findPlanBookmarkByPlanAndMember(plan, member);
        if (planBookmark == null) {
            PlanBookmark newPlanBookmark = PlanBookmark.builder()
                    .plan(plan)
                    .member(member)
                    .build();
            planBookmarkRepository.save(newPlanBookmark);
        } else {
            // 북마크 해체
            planBookmarkRepository.delete(planBookmark);
        }
    }

    @Transactional
    // 북마크 상태변경
    public PlanBookMarkStateRes findPlanBookmark(Long memberId, Long planId){
        Member member = memberService.findMemberById(memberId);
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND_PLAN_EXCEPTION));
        PlanBookmark planBookmark = planBookmarkRepository.findPlanBookmarkByPlanAndMember(plan, member);

        Boolean isMark = true;
        if (planBookmark == null) {
            isMark=false;
        }
        return new PlanBookMarkStateRes(isMark);
    }

    private Place getPlace(Long placeId){
        return placeRepository.findById(placeId).orElseThrow(
                ()->new ApplicationException(ErrorCode.NOT_FOUND_PLACE_EXCEPTION));
    }

    public List<PlaceBookmarkRes> getPlaceBookmarks(Long memberId) {
        List<PlaceBookmarkRes> list = new ArrayList<>();
        Member member = memberService.findMemberById(memberId);

        List<PlaceBookmark> placeBookmarkList = placeBookmarkRepository.findAllByMemberOrderByCreatedAtDesc(member);
        placeBookmarkList.forEach(
                placeBookmark ->
                        list.add(PlaceBookmarkRes.of(placeBookmark.getPlace(),
                                disabilityPlaceCategoryRepository.findDisabilityCategoryIds(placeBookmark.getPlace().getId())))

        );

        return list;
    }

    public List<PlanBookmarkRes> getPlanBookmarks(Long memberId) {
        List<PlanBookmarkRes> list = new ArrayList<>();
        Member member = memberService.findMemberById(memberId);

        List<PlanBookmark> planBookmarkList = planBookmarkRepository.findAllByMemberOrderByCreatedAtDesc(member);
        planBookmarkList.forEach( planBookmark -> {
            list.add(PlanBookmarkRes.of(planBookmark.getPlan(),s3Client.getUrl()+planBookmark.getPlan().getMember().getProfileUuid()+"/profile_"+planBookmark.getPlan().getMember().getProfileUuid(),getPlanImageUrl(planBookmark.getPlan())));
        });

        return list;
    }

    private String getPlanImageUrl(Plan plan){
        List<Day> dayList = dayRepository.findByPlanOrderByCreatedAtDesc(plan);
        if (dayList.isEmpty() || dayList == null) {
            return null;
        }
        return dayList.get(0).getPlace().getFirstImg();
    }

}
