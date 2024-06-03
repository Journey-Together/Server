package Journey.Together.domain.dairy.dto;

import java.time.LocalDate;

public record DaliyPlace(
        LocalDate date,
        Long placeId
) {
}
