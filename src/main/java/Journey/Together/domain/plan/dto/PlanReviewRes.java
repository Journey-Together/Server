package Journey.Together.domain.plan.dto;

import jakarta.validation.constraints.Null;
import lombok.Builder;

@Builder
public record PlanReviewRes(
        @Null
        Long reviewId,
        @Null
        String content,
        @Null
        Float grade,
        @Null
        boolean isWriter,
        @Null
        boolean hasReview
) {
    public static PlanReviewRes of(Long reviewId,
                                   String content,
                                   Float grade,boolean isWriter,boolean hasReview){
        return PlanReviewRes.builder()
                .reviewId(reviewId)
                .content(content)
                .grade(grade)
                .isWriter(isWriter)
                .hasReview(hasReview)
                .build();
    }
}
