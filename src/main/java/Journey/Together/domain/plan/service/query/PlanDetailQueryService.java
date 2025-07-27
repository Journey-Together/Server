package Journey.Together.domain.plan.service.query;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.repository.DisabilityPlaceCategoryRepository;
import Journey.Together.domain.plan.dto.DailyList;
import Journey.Together.domain.plan.dto.DailyPlaceInfo;
import Journey.Together.domain.plan.dto.PlanDetailRes;
import Journey.Together.domain.plan.entity.Day;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.repository.DayRepository;
import Journey.Together.domain.plan.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanDetailQueryService {

    private final DayRepository dayRepository;
    private final DisabilityPlaceCategoryRepository disabilityPlaceCategoryRepository;
    private final PlanQueryService planQueryService;

    public PlanDetailRes getDetail(Member member, Plan plan) {
        boolean isWriter = member != null && plan.getMember().getMemberId().equals(member.getMemberId());

        List<Day> dayList = dayRepository.findAllByMemberAndPlanOrderByDateAsc(plan.getMember(), plan);
        List<String> imageUrls = planQueryService.getPlaceImageListOfPlan(plan); // 분리된 로직 사용

        Map<LocalDate, List<Day>> groupedByDate = dayList.stream()
                .collect(Collectors.groupingBy(Day::getDate));

        List<DailyList> dailyLists = new ArrayList<>();
        LocalDate startDate = plan.getStartDate();
        LocalDate endDate = plan.getEndDate();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<Day> days = groupedByDate.get(date);
            List<DailyPlaceInfo> dailyPlaceInfoList = new ArrayList<>();
            if (days != null) {
                for (Day day : days) {
                    List<Long> disabilityCategoryList =
                            disabilityPlaceCategoryRepository.findDisabilityCategoryIds(day.getPlace().getId());
                    dailyPlaceInfoList.add(DailyPlaceInfo.of(day.getPlace(), disabilityCategoryList));
                }
            }
            dailyLists.add(DailyList.of(date, dailyPlaceInfoList));
        }

        dailyLists.sort(Comparator.comparing(DailyList::getDate));
        String remainDate = DateUtil.getRemainDateString(startDate, endDate);

        return PlanDetailRes.of(imageUrls, dailyLists, isWriter, plan, remainDate);
    }
}

