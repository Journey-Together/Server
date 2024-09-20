package Journey.Together.domain.report.dto;

import Journey.Together.domain.report.entity.Report;

import java.time.LocalDateTime;
import java.util.List;

public record ReportDto (
        Long reportId,
        String reason,
        String description,
        Boolean approval,
        LocalDateTime reportTime,
        ReviewDto reviewDto){
    public static ReportDto of(Report report, ReviewDto reviewDto){
        return new ReportDto(report.getId(), report.getReason(), report.getDescription(),
                report.getApproval(), report.getCreatedAt(), reviewDto);
    }
}
