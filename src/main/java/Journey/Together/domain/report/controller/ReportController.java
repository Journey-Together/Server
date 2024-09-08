package Journey.Together.domain.report.controller;

import Journey.Together.domain.report.dto.ReportReq;
import Journey.Together.domain.report.enumerate.ReviewType;
import Journey.Together.domain.report.service.ReportService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/report")
@Tag(name = "Report", description = "신고하기 관련 API")
public class ReportController {
    private final ReportService reportService;

    @PostMapping()
    public ApiResponse<?> createReport(@RequestBody ReportReq reportReq,
                                       @RequestParam String reviewType){
        reportService.createReport(reportReq, reviewType);
        return ApiResponse.success(Success.CREATE_RERORT_SUCCESS);
    }

}
