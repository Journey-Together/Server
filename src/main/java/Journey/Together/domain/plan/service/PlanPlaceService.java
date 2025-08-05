package Journey.Together.domain.plan.service;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.domain.plan.dto.DailyPlace;
import Journey.Together.domain.plan.entity.Day;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.repository.DayRepository;
import Journey.Together.domain.plan.service.factory.PlanFactory;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanPlaceService {
    private final DayRepository dayRepository;
    private final PlaceRepository placeRepository;
    private final PlanFactory planFactory;

    public void savePlacesByDay(List<DailyPlace> places, Member member, Plan plan) {
        // 모든 placeId 수집
        List<Long> allPlaceIds = places.stream()
                .flatMap(dp -> dp.places().stream())
                .distinct()
                .toList();

        Map<Long, Place> placeMap = placeRepository.findAllById(allPlaceIds).stream()
                .collect(Collectors.toMap(Place::getId, p -> p));

        // 사용 시 Map에서 꺼내기
        List<Day> daysToSave = new ArrayList<>();
        for (DailyPlace dailyPlace : places) {
            for (Long placeId : dailyPlace.places()) {
                Place place = Optional.ofNullable(placeMap.get(placeId))
                        .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION));

                Day day = planFactory.createDay(member, plan, place, dailyPlace.date());
                daysToSave.add(day);
            }
        }
    }

    @Transactional
    public void updatePlacesByDay(Member member, Plan plan, List<DailyPlace> dailyPlaces) {
        List<Day> existingDays = dayRepository.findAllByMemberAndPlan(member, plan);

        Set<String> existingKeySet = existingDays.stream()
                .map(day -> day.getDate().toString() + "-" + day.getPlace().getId())
                .collect(Collectors.toSet());

        Set<String> newKeySet = new HashSet<>();
        Set<Long> newPlaceIds = new HashSet<>();
        List<Day> daysToSave = new ArrayList<>();

        for (DailyPlace dailyPlace : dailyPlaces) {
            for (Long placeId : dailyPlace.places()) {
                String key = dailyPlace.date().toString() + "-" + placeId;
                newKeySet.add(key);
                newPlaceIds.add(placeId);
            }
        }

        Map<Long, Place> placeMap = placeRepository.findAllById(newPlaceIds).stream()
                .collect(Collectors.toMap(Place::getId, p -> p));

        for (DailyPlace dailyPlace : dailyPlaces) {
            for (Long placeId : dailyPlace.places()) {
                String key = dailyPlace.date().toString() + "-" + placeId;
                if (!existingKeySet.contains(key)) {
                    Place place = Optional.ofNullable(placeMap.get(placeId))
                            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION));
                    Day day = planFactory.createDay(member, plan, place, dailyPlace.date());
                    daysToSave.add(day);
                }
            }
        }

        Set<String> keysToDelete = new HashSet<>(existingKeySet);
        keysToDelete.removeAll(newKeySet);

        List<Day> daysToDelete = existingDays.stream()
                .filter(day -> keysToDelete.contains(day.getDate().toString() + "-" + day.getPlace().getId()))
                .toList();

        dayRepository.deleteAll(daysToDelete);
        dayRepository.saveAll(daysToSave);
    }
}
