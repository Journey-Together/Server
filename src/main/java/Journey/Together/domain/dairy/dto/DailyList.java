package Journey.Together.domain.dairy.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record DailyList(
        String date,
        List<DailyPlaceInfo> dailyPlaceInfoList
) {
    public static DailyList of(String date,
                               List<DailyPlaceInfo> dailyPlaceInfoList){
        return DailyList.builder()
                .date(date)
                .dailyPlaceInfoList(dailyPlaceInfoList)
                .build();
    }
}
