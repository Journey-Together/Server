package Journey.Together.domain.plan.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record DailyList(
        LocalDate date,
        List<DailyPlaceInfo> dailyPlaceInfoList
) {
    public static DailyList of(LocalDate date,
                               List<DailyPlaceInfo> dailyPlaceInfoList){
        return DailyList.builder()
                .date(date)
                .dailyPlaceInfoList(dailyPlaceInfoList)
                .build();
    }
}
