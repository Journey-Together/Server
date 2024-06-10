package Journey.Together.domain.dairy.dto;

import java.time.LocalDate;
import java.util.List;

public record DailyPlace(
        LocalDate date,
        List<Long> places
) {
}
