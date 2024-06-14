package Journey.Together.domain.plan.dto;

public record PlanReviewReq(
        float grade,
        String content,
        Boolean isPublic
) {
}
