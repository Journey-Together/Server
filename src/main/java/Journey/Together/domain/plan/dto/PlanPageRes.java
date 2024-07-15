package Journey.Together.domain.plan.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PlanPageRes(
        List<PlanRes> planResList,
        int pageNo,
        int pageSize,
        int totalPages,
        boolean last
) {
    public static PlanPageRes of(List<PlanRes> planResList,
                                      int pageNo,
                                      int pageSize,
                                      int totalPages,
                                      boolean last){
        return PlanPageRes.builder()
                .planResList(planResList)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .last(last)
                .build();
    }
}
