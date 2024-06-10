package Journey.Together.domain.dairy.dto;

import Journey.Together.domain.dairy.entity.Plan;
import Journey.Together.domain.member.entity.Member;
import lombok.Builder;

import java.util.List;

@Builder
public record PlanDetailRes(
        List<String> imageUrls,
        List<DailyList> dailyList,
        Boolean isWriter,
        Long writerId,
        String writerNickname
) {
    public static PlanDetailRes of(List<String> imageUrls,
                                   List<DailyList> dailyList, Boolean isWriter, Plan plan){
        return PlanDetailRes.builder()
                .imageUrls(imageUrls)
                .dailyList(dailyList)
                .isWriter(isWriter)
                .writerId(plan.getMember().getMemberId())
                .writerNickname(plan.getMember().getNickname())
                .build();
    }
}
