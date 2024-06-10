package Journey.Together.domain.place.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record UpdateReviewDto(
        @Nullable
        @JsonFormat(pattern = "yyyy-MM-dd")
        Float grade,
        @Nullable
        LocalDate date,
        @Nullable
        String content,
        @Nullable
        List<String> deleteImgUrls

) {
}
