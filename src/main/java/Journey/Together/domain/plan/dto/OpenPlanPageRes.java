package Journey.Together.domain.plan.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record OpenPlanPageRes(
        List<OpenPlanRes> openPlanResList,
        int pageNo,
        int pageSize,
        int totalPages,
        boolean last
) {
    public static OpenPlanPageRes of(List<OpenPlanRes> openPlanResList,
                                 int pageNo,
                                 int pageSize,
                                 int totalPages,
                                 boolean last){
        return OpenPlanPageRes.builder()
                .openPlanResList(openPlanResList)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .last(last)
                .build();
    }
}