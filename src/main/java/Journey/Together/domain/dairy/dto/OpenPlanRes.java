package Journey.Together.domain.dairy.dto;

import Journey.Together.domain.dairy.entity.Plan;
import lombok.Builder;

import java.time.Period;

@Builder
public record OpenPlanRes(
        Long planId,
        String title,
        String placeImageUrl,
        Long memberId,
        String memberName,
        String memberImageUrl,
        String date
) {
    public static OpenPlanRes of(Plan plan,String memberImageUrl,String placeImageUrl){
        Period period = Period.between(plan.getStartDate(),plan.getEndDate());
        String date = period.getDays()+"일일정";

        return OpenPlanRes.builder()
                .planId(plan.getPlanId())
                .title(plan.getTitle())
                .placeImageUrl(placeImageUrl)
                .memberId(plan.getMember().getMemberId())
                .memberName(plan.getMember().getName())
                .memberImageUrl(memberImageUrl)
                .date(date)
                .build();
    }
}
