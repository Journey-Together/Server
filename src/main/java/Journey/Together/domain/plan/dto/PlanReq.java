package Journey.Together.domain.plan.dto;

import java.time.LocalDate;
import java.util.List;

public record PlanReq(
        String title,
        LocalDate startDate,
        LocalDate endDate,
        List<DailyPlace> dailyplace
) {
}
