package Journey.Together.domain.plan.dto;

import Journey.Together.domain.plan.entity.PlanReview;
import lombok.Builder;

import java.util.List;

@Builder
public record MyReviewRes(
        float grade,
        String content,
        List<String> imageList
) {
    public static MyReviewRes of(PlanReview planReview,List<String> imageList){
        return MyReviewRes.builder()
                .grade(planReview.getGrade())
                .content(planReview.getContent())
                .imageList(imageList)
                .build();
    }
}
