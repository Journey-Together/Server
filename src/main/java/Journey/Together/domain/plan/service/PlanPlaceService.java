package Journey.Together.domain.plan.service;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.domain.plan.dto.DailyPlace;
import Journey.Together.domain.plan.entity.Day;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.service.factory.PlanFactory;
import Journey.Together.domain.plan.repository.DayRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanPlaceService {
    private final PlaceRepository placeRepository;
    private final DayRepository dayRepository;
    private final PlanFactory planFactory;

    public void savePlacesByDay(List<DailyPlace> places, Member member, Plan plan) {
        for (DailyPlace dailyPlace : places) {
            for (Long placeId : dailyPlace.places()) {
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION));
                Day day = planFactory.createDay(member, plan, place, dailyPlace.date());
                dayRepository.save(day);
            }
        }
    }
}
