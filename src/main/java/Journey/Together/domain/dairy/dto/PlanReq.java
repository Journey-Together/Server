package Journey.Together.domain.dairy.dto;

import java.time.LocalDate;
import java.util.List;

public record PlanReq(
        String title,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isPublic,
        List<DailyPlace> dailyplace
) {
}
