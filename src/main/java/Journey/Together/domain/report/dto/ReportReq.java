package Journey.Together.domain.report.dto;

import Journey.Together.domain.report.enumerate.ReviewType;
import jakarta.validation.constraints.NotNull;


public record ReportReq (
       @NotNull Long review_id,
       @NotNull String reason,
       String description

){

}