package Journey.Together.domain.plan.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdatePlanReviewReq(
        @Null
        Float grade,
        @Null
        String content
) {
}
