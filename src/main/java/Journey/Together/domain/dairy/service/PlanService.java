package Journey.Together.domain.dairy.service;

import Journey.Together.domain.dairy.dto.DaliyPlace;
import Journey.Together.domain.dairy.dto.PlanReq;
import Journey.Together.domain.dairy.entity.Day;
import Journey.Together.domain.dairy.entity.Plan;
import Journey.Together.domain.dairy.repository.DayRepository;
import Journey.Together.domain.dairy.repository.PlanRepository;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.repository.MemberRepository;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanService {
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final DayRepository dayRepository;
    private final PlaceRepository placeRepository;

    public void savePlan(Member member, PlanReq planReq){
        // Validation
        memberRepository.findMemberByEmailAndDeletedAtIsNull(member.getEmail()).orElseThrow(()->new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION));
        //Buisness
        Plan plan = Plan.builder()
                .member(member)
                .title(planReq.title())
                .startDate(planReq.startDate())
                .endDate(planReq.endDate())
                .isPublic(planReq.isPublic())
                .build();
        planRepository.save(plan);
        System.out.println("저장");
        //날짜별 장소 정보 저장
        for(DaliyPlace daliyPlace : planReq.daliyplace()){
            Place place = placeRepository.findPlaceById(daliyPlace.placeId());
            if(place == null){
                throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
            }
            Day day = Day.builder()
                    .member(member)
                    .plan(plan)
                    .place(place)
                    .date(daliyPlace.date())
                    .build();
            dayRepository.save(day);
        }
    }
}
