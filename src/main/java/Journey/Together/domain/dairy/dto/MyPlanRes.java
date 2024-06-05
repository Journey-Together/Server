package Journey.Together.domain.dairy.dto;

import Journey.Together.domain.dairy.entity.Plan;
import jakarta.validation.constraints.Null;
import lombok.Builder;

import java.time.LocalDate;
@Builder
public record MyPlanRes(
        Long planId,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String imageUrl,
        @Null
        String remainDate,
        @Null
        Boolean hasReview
) {
    public static MyPlanRes of (Plan plan,String imageUrl,String remainDate,Boolean hasReview){
        return MyPlanRes.builder()
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
