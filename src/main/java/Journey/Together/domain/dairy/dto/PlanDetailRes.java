package Journey.Together.domain.dairy.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PlanDetailRes(
        List<String> imageUrls,
        List<DailyList> dailyList
) {
    public static PlanDetailRes of(List<String> imageUrls,
                                   List<DailyList> dailyList){
        return PlanDetailRes.builder()
                .imageUrls(imageUrls)
                .dailyList(dailyList)
                .build();
    }
}
