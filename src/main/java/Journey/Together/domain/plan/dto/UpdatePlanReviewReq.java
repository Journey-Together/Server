package Journey.Together.domain.plan.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdatePlanReviewReq(
        @Null
        Float grade,
        @Null
        String content,
        @Null
        List<String> deleteImgUrls
) {
}
