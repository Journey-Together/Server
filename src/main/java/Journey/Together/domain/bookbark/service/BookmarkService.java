package Journey.Together.domain.bookbark.service;

import Journey.Together.domain.bookbark.dto.PlaceBookmarkRes;
import Journey.Together.domain.bookbark.entity.PlaceBookmark;
import Journey.Together.domain.bookbark.entity.PlanBookmark;
import Journey.Together.domain.bookbark.entity.PlanBookmarkRes;
import Journey.Together.domain.bookbark.repository.PlaceBookmarkRepository;
import Journey.Together.domain.bookbark.repository.PlanBookmarkRepository;
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

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final PlaceBookmarkRepository placeBookmarkRepository;
    private final PlanBookmarkRepository planBookmarkRepository;
    private final DisabilityPlaceCategoryRepository disabilityPlaceCategoryRepository;
    private final PlaceRepository placeRepository;

    private final DayRepository dayRepository;
    private final PlanRepository planRepository;


    private final S3Client s3Client;


    //북마크한 여행지 이름만 가져오기
    public List<PlaceBookmarkDto> getBookmarkPlaceNames(Member member){
        List<PlaceBookmark> placeBookmarkList = placeBookmarkRepository.findAllByMemberOrderByPlaceNameAsc(member);
        if(placeBookmarkList.isEmpty() || placeBookmarkList==null)
            return new ArrayList<>();

        return placeBookmarkList.stream().map(PlaceBookmarkDto::of).toList();
    }


    @Transactional
    // 북마크 상태변경
    public void placeBookmark(Member member, Long placeId){
        Place place = getPlace(placeId);

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
    public void planBookmark(Member member, Long planId){
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

    private Place getPlace(Long placeId){
        return placeRepository.findById(placeId).orElseThrow(
                ()->new ApplicationException(ErrorCode.NOT_FOUND_PLACE_EXCEPTION));
    }

    public List<PlaceBookmarkRes> getPlaceBookmarks(Member member) {
        List<PlaceBookmarkRes> list = new ArrayList<>();

        List<PlaceBookmark> placeBookmarkList = placeBookmarkRepository.findAllByMemberOrderByCreatedAtDesc(member);
        placeBookmarkList.forEach(
                placeBookmark ->
                        list.add(PlaceBookmarkRes.of(placeBookmark.getPlace(),
                                disabilityPlaceCategoryRepository.findDisabilityCategoryIds(placeBookmark.getPlace().getId())))

        );

        return list;
    }

    public List<PlanBookmarkRes> getPlanBookmarks(Member member) {
        List<PlanBookmarkRes> list = new ArrayList<>();

        List<PlanBookmark> planBookmarkList = planBookmarkRepository.findAllByMemberOrderByCreatedAtDesc(member);
        planBookmarkList.forEach( planBookmark -> {
            list.add(PlanBookmarkRes.of(planBookmark.getPlan(),s3Client.getUrl()+planBookmark.getPlan().getMember().getProfileUuid()+"/profile",getPlanImageUrl(member, planBookmark.getPlan())));
        });

        return list;
    }

    private String getPlanImageUrl(Member member, Plan plan){
        List<Day> dayList = dayRepository.findByMemberAndDateAndPlanOrderByCreatedAtDesc(member,plan.getStartDate(),plan);
        if (dayList.isEmpty() || dayList == null) {
            return null;
        }
        return dayList.get(0).getPlace().getFirstImg();
    }

}
