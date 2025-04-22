package Journey.Together.domain.report.controller;

import Journey.Together.domain.report.dto.ApprovalDto;
import Journey.Together.domain.report.dto.ReportReq;
import Journey.Together.domain.report.dto.ReportRes;
import Journey.Together.domain.report.enumerate.ReviewType;
import Journey.Together.domain.report.service.ReportService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.config.PublicEndpoint;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/report")
@PublicEndpoint
@Tag(name = "Report", description = "신고하기 관련 API")
public class ReportController {
    private final ReportService reportService;

    @PostMapping()
    public ApiResponse<?> createReport(@RequestBody ReportReq reportReq,
                                       @RequestParam String reviewType){
        reportService.createReport(reportReq, reviewType);
        return ApiResponse.success(Success.CREATE_RERORT_SUCCESS);
    }


    @GetMapping()
    public ApiResponse<ReportRes> getReports(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             @RequestParam(required = false) Boolean approval,
                                             @RequestParam(required = false) String reason,
                                             @RequestParam(required = false) String reviewType,
                                             @PageableDefault(size = 10,page = 0) Pageable pageable
                                     ){
        return ApiResponse.success(Success.CREATE_RERORT_SUCCESS,
                reportService.getReports(principalDetails.getMember(), approval, reason, reviewType, pageable));
    }

    @PatchMapping()
    public ApiResponse<?> setAprrovalStatus(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                            @RequestBody ApprovalDto approvalDto){
            reportService.setApprovalStatus(principalDetails.getMember(), approvalDto);
        return ApiResponse.success(Success.UPDATE_APPROVAL_SUCCESS);
    }

}
