package Journey.Together.domain.place.dto.request;

import jakarta.annotation.Nullable;

import java.time.LocalDate;

public record UpdateReviewDto(
        @Nullable
        Float grade,
        @Nullable
        LocalDate date,
        @Nullable
        String content

) {
}
