package Journey.Together.domain.dairy.dto;

import java.time.LocalDate;

public record DailyPlace(
        LocalDate date,
        Long placeId
) {
}
