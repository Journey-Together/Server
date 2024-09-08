package Journey.Together.domain.report.repository;

import Journey.Together.domain.place.dto.response.SearchPlace;
import Journey.Together.domain.report.dto.FilterReport;
import Journey.Together.domain.report.entity.QReport;
import Journey.Together.domain.report.entity.Report;
import Journey.Together.domain.report.enumerate.ReviewType;
import Journey.Together.domain.report.service.ReportService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import org.springframework.data.domain.Pageable;
import java.util.List;

import static Journey.Together.domain.place.entity.QPlace.place;
import static Journey.Together.domain.report.entity.QReport.report;

public class ReportRepositoryImpl  implements ReportRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public ReportRepositoryImpl(EntityManager em){

        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public FilterReport getReportList(Boolean approval, String reason,
                                      String reviewType, Pageable pageable) {
        List<Report> reports = queryFactory
                .selectDistinct(report)
                .from(report)
                .where(approvalEq(approval),reasonEq(reason), reviewTypeEq(reviewType))
                .orderBy(report.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(report.countDistinct())
                .from(report)
                .where(approvalEq(approval),reasonEq(reason), reviewTypeEq(reviewType))
                .orderBy(report.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchOne();

        return new FilterReport(reports,total);
    }

    private BooleanExpression reviewTypeEq(String reviewType) {
        return reviewType != null ?  report.reviewType.eq(ReviewType.valueOf(reviewType)) : null;
    }

    private BooleanExpression reasonEq(String reason) {
        return reason != null ?  report.reason.eq(reason) : null;
    }

    private BooleanExpression approvalEq(Boolean approval) {
        return approval != null ?  report.approval.eq(approval) : null;
    }

}
