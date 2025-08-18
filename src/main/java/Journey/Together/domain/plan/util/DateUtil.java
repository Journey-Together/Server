package Journey.Together.domain.plan.util;

import java.time.LocalDate;
import java.time.Period;

public class DateUtil {
    private DateUtil() {} // 인스턴스 생성 방지

    public static String getRemainDateString(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        if ((today.isEqual(startDate) || today.isAfter(startDate))
                && (today.isEqual(endDate) || today.isBefore(endDate))) {
            return "D-DAY";
        } else if (today.isBefore(startDate)) {
            Period period = Period.between(today, startDate);
            return "D-" + period.getDays();
        }
        return null;
    }
}
