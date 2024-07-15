package Journey.Together.domain.plan.dto;

import lombok.Builder;

import java.util.List;
@Builder
public record PlaceInfoPageRes(
        List<PlaceInfo> placeInfoList,
        int pageNo,
        int pageSize,
        int totalPages,
        boolean last,
        long totalElements
) {
    public static PlaceInfoPageRes of(List<PlaceInfo> placeInfoList,
                                      int pageNo,
                                      int pageSize,
                                      int totalPages,
                                      boolean last,
                                      long totalElements){
        return PlaceInfoPageRes.builder()
                .placeInfoList(placeInfoList)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .last(last)
                .totalElements(totalElements)
                .build();
    }
}
