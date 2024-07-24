package Journey.Together.domain.plan.dto;

import jakarta.validation.constraints.Null;
import lombok.Builder;

import java.util.List;

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
        boolean hasReview,
        @Null
        List<String> imageList
) {
    public static PlanReviewRes of(Long reviewId,
                                   String content,
                                   Float grade,boolean isWriter,boolean hasReview,List<String> imageList){
        return PlanReviewRes.builder()
                .reviewId(reviewId)
                .content(content)
                .grade(grade)
                .isWriter(isWriter)
                .hasReview(hasReview)
                .imageList(imageList)
                .build();
    }
}
