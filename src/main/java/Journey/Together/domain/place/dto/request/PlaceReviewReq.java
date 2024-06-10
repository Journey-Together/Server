package Journey.Together.domain.place.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.cglib.core.Local;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PlaceReviewReq(
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        Float grade,
        String content
) {
}
