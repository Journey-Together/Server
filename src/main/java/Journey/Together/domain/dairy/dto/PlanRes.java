package Journey.Together.domain.dairy.dto;

import Journey.Together.domain.dairy.entity.Plan;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;
import lombok.Builder;

import java.time.LocalDate;
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlanRes(
        Long planId,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        Object imageUrl,
        @Null
        String remainDate,
        @Null
        Boolean hasReview
) {
    public static PlanRes of (Plan plan, String imageUrl, String remainDate, Boolean hasReview){
        return PlanRes.builder()
                .planId(plan.getPlanId())
                .title(plan.getTitle())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .imageUrl(imageUrl)
                .remainDate(remainDate)
                .hasReview(hasReview)
                .build();
    }
}
