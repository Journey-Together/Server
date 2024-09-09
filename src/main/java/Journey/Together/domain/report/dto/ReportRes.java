package Journey.Together.domain.report.dto;

import java.util.List;

public record ReportRes(
    Long reportNum,
    List<ReportDto> reportResultList,
    Integer pageNo,
    Integer pageSize
){

}
