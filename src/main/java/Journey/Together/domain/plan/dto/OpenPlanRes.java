package Journey.Together.domain.plan.dto;

import Journey.Together.domain.plan.entity.Plan;
import lombok.Builder;

import java.time.Period;

@Builder
public record OpenPlanRes(
        Long planId,
        String title,
        String imageUrl,
        Long memberId,
        String memberNickname,
        String memberImageUrl,
        String date
) {
    public static OpenPlanRes of(Plan plan,String memberImageUrl,String imageUrl){
        Period period = Period.between(plan.getStartDate(),plan.getEndDate());
        String date = (period.getDays()+1)+"일일정";

        return OpenPlanRes.builder()
                .planId(plan.getPlanId())
                .title(plan.getTitle())
                .imageUrl(imageUrl)
                .memberId(plan.getMember().getMemberId())
                .memberNickname(plan.getMember().getNickname())
                .memberImageUrl(memberImageUrl)
                .date(date)
                .build();
    }
}
