package Journey.Together.domain.plan.service.query;

import Journey.Together.domain.plan.entity.Day;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.repository.DayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanQueryService {
    private final DayRepository dayRepository;

    public String getFirstPlaceImageOfPlan(Plan plan) {
        List<Day> dayList = dayRepository.findByPlanOrderByCreatedAtDesc(plan);
        if (!dayList.isEmpty()) {
            String placeImageUrl = dayList.get(0).getPlace().getFirstImg();
            return placeImageUrl.isEmpty() ? null : placeImageUrl;
        }
        return null;
    }

    public List<String> getPlaceImageListOfPlan(Plan plan) {
        List<Day> dayList = dayRepository.findByPlanOrderByCreatedAtDesc(plan);
        List<String> list = new ArrayList<>();
        if (!dayList.isEmpty()) {
            dayList.forEach(day -> {
                list.add(day.getPlace().getFirstImg());
            });
            return list;
        }
        return null;
    }
}
