package Journey.Together.domain.report.service;

import Journey.Together.domain.place.entity.PlaceReview;
import Journey.Together.domain.place.repository.PlaceReviewRepository;
import Journey.Together.domain.plan.entity.PlanReview;
import Journey.Together.domain.plan.repository.PlanReviewRepository;
import Journey.Together.domain.report.dto.ReportReq;
import Journey.Together.domain.report.entity.Report;
import Journey.Together.domain.report.enumerate.ReviewType;
import Journey.Together.domain.report.repository.ReportRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final PlanReviewRepository planReviewRepository;
    private final PlaceReviewRepository placeReviewRepository;

    @Transactional
    public void createReport(ReportReq reportReq, String reviewType){
        if(Objects.equals(reportReq.reason(), "기타") && reportReq.description()==null){
            throw new ApplicationException(ErrorCode.NOT_FOUND_DESCRIPTION_EXCEPTION);
        }

        if(reviewType.equals("PlanReview")){
            PlanReview planReview = planReviewRepository.findById(reportReq.review_id()).orElseThrow(
                    () -> new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION)
            );
            planReview.setReport(true);
            reportRepository.save(reportBuilder(reportReq,ReviewType.PlanReview));

        }else if(reviewType.equals("PlaceReview")){
            PlaceReview placeReview = placeReviewRepository.findById(reportReq.review_id()).orElseThrow(
                    () -> new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION)
            );
            placeReview.setReport(true);
            reportRepository.save(reportBuilder(reportReq,ReviewType.PlaceReview));
        }else {
            throw new ApplicationException(ErrorCode.REVIEW_TYPE_EXCEPTION);
        }

    }

    public Report reportBuilder(ReportReq reportReq,ReviewType reviewType){
        if(reportReq.description() != null){
            return Report.builder().review_id(reportReq.review_id())
                    .reason(reportReq.reason())
                    .description(reportReq.description())
                    .reviewType(reviewType).build();
        }else{
            return Report.builder().review_id(reportReq.review_id())
                    .reason(reportReq.reason())
                    .reviewType(reviewType).build();
        }
    }
}
