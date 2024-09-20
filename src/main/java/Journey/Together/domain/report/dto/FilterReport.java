package Journey.Together.domain.report.dto;

import Journey.Together.domain.report.entity.Report;

import java.util.List;

public record FilterReport(
        List<Report> reports,
        Long size
) {
}
