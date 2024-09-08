package Journey.Together.domain.report.repository;

import Journey.Together.domain.report.dto.FilterReport;
import Journey.Together.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ReportRepositoryCustom {
    FilterReport getReportList(Boolean approval, String reason, String reviewType, Pageable pageable);
}
