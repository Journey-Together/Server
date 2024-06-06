package Journey.Together.domain.dairy.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PlanDetailRes(
        List<String> imageUrls,
        List<DailyList> dailyList,
        Boolean isWriter
) {
    public static PlanDetailRes of(List<String> imageUrls,
                                   List<DailyList> dailyList,Boolean isWriter){
        return PlanDetailRes.builder()
                .imageUrls(imageUrls)
                .dailyList(dailyList)
                .isWriter(isWriter)
                .build();
    }
}
