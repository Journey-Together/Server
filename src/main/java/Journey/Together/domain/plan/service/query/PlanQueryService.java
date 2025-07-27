package Journey.Together.domain.plan.service.query;

import Journey.Together.domain.plan.entity.Day;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.repository.DayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
