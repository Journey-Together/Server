package Journey.Together.domain.plan.service.query;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.plan.dto.MyPlanRes;
import Journey.Together.domain.plan.entity.Day;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.repository.DayRepository;
import Journey.Together.domain.plan.repository.PlanRepository;
import Journey.Together.domain.plan.repository.PlanReviewRepository;
import Journey.Together.domain.plan.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanQueryService {
    private final DayRepository dayRepository;
    private final PlanRepository planRepository;
    private final PlanReviewRepository planReviewRepository;

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

    @Transactional(readOnly = true)
    public List<MyPlanRes> findMyPlans(Member member) {
        List<Plan> plans = planRepository.findAllByMemberAndDeletedAtIsNull(member);

        List<Plan> top3 = plans.stream()
                .sorted(Comparator.comparingLong(plan ->
                        Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), plan.getStartDate()))))
                .limit(3)
                .toList();

        return top3.stream()
                .map(plan -> {
                    String image = getFirstPlaceImageOfPlan(plan);
                    String remainDate = DateUtil.getRemainDateString(plan.getStartDate(), plan.getEndDate());
                    Boolean hasReview = null;

                    if (LocalDate.now().isAfter(plan.getEndDate())) {
                        hasReview = planReviewRepository.existsAllByPlanAndDeletedAtIsNull(plan);
                    }

                    return MyPlanRes.of(plan, image, remainDate, hasReview);
                })
                .collect(Collectors.toList());
    }

}
