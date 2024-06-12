package Journey.Together.domain.plan.dto;

import Journey.Together.domain.plan.entity.Plan;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record PlanDetailRes(
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String remainDate,
        Boolean isPublic,
        List<String> imageUrls,
        List<DailyList> dailyList,
        Boolean isWriter,
        Long writerId,
        String writerNickname
) {
    public static PlanDetailRes of(List<String> imageUrls,
                                   List<DailyList> dailyList, Boolean isWriter, Plan plan,String remainDate){
        return PlanDetailRes.builder()
                .title(plan.getTitle())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .remainDate(remainDate)
                .isPublic(plan.getIsPublic())
                .imageUrls(imageUrls)
                .dailyList(dailyList)
                .isWriter(isWriter)
                .writerId(plan.getMember().getMemberId())
                .writerNickname(plan.getMember().getNickname())
                .build();
    }
}
