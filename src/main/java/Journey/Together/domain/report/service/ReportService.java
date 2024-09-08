package Journey.Together.domain.report.service;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.enumerate.MemberType;
import Journey.Together.domain.place.dto.response.PlaceRes;
import Journey.Together.domain.place.entity.PlaceReview;
import Journey.Together.domain.place.entity.PlaceReviewImg;
import Journey.Together.domain.place.repository.PlaceReviewImgRepository;
import Journey.Together.domain.place.repository.PlaceReviewRepository;
import Journey.Together.domain.plan.entity.PlanReview;
import Journey.Together.domain.plan.entity.PlanReviewImage;
import Journey.Together.domain.plan.repository.PlanReviewImageRepository;
import Journey.Together.domain.plan.repository.PlanReviewRepository;
import Journey.Together.domain.report.dto.*;
import Journey.Together.domain.report.entity.Report;
import Journey.Together.domain.report.enumerate.ReviewType;
import Journey.Together.domain.report.repository.ReportRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final PlanReviewRepository planReviewRepository;
    private final PlaceReviewRepository placeReviewRepository;
    private final PlaceReviewImgRepository placeReviewImgRepository;
    private final PlanReviewImageRepository planReviewImageRepository;

    @Transactional
    public void createReport(ReportReq reportReq, String reviewType){
        if(Objects.equals(reportReq.reason(), "기타") && reportReq.description()==null){
            throw new ApplicationException(ErrorCode.NOT_FOUND_DESCRIPTION_EXCEPTION);
        }

        if(reviewType.equals("PlanReview")){
            PlanReview planReview = planReviewRepository.findById(reportReq.review_id()).orElseThrow(
                    () -> new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION)
            );
            reportRepository.save(reportBuilder(reportReq,ReviewType.PlanReview));

        }else if(reviewType.equals("PlaceReview")){
            PlaceReview placeReview = placeReviewRepository.findById(reportReq.review_id()).orElseThrow(
                    () -> new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION)
            );
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

    public ReportRes getReports(Member member, Boolean approval, String reason, String reviewType, Pageable pageable) {
        if(!member.getMemberType().equals(MemberType.ADMIN)){
            throw new ApplicationException(ErrorCode.WRONG_ACCESS_EXCEPTION);
        }

        List<ReportDto> reportDtoList =new ArrayList<>();

        FilterReport filterReport = reportRepository.getReportList(approval, reason, reviewType, pageable);
        filterReport.reports().forEach(
                report -> reportDtoList.add(ReportDto.of(report,getReviewDto(report)))
        );

        return new ReportRes(filterReport.size(), reportDtoList, pageable.getPageNumber(),pageable.getPageSize() );
    }

    public ReviewDto getReviewDto(Report report){
        if(report.getReviewType()== ReviewType.PlaceReview){
            PlaceReview placeReview = placeReviewRepository.findPlaceReviewById(report.getReview_id());
            List<String> imgList = placeReviewImgRepository.findAllByPlaceReview(placeReview).stream().map(PlaceReviewImg::getImgUrl).toList();
            return new ReviewDto(report.getReview_id(), placeReview.getMember().getMemberId(), placeReview.getMember().getName(), report.getReviewType(), placeReview.getContent(), imgList);
        } else if (report.getReviewType()== ReviewType.PlanReview) {
            PlanReview planReview = planReviewRepository.findPlanReviewByPlanReviewId(report.getReview_id());
            List<String> imgList = planReviewImageRepository.findPlanReviewImageByPlanReview(planReview).stream().map(PlanReviewImage::getImageUrl).toList();
            return new ReviewDto(report.getReview_id(), planReview.getMember().getMemberId(), planReview.getMember().getName(), report.getReviewType(), planReview.getContent(), imgList);
        }
        else{
            throw new ApplicationException(ErrorCode.INTERNAL_SERVER_EXCEPTION);
        }
    }

    @Transactional
    public void setApprovalStatus(Member member, ApprovalDto approvalDto) {
        if(!member.getMemberType().equals(MemberType.ADMIN)){
            throw new ApplicationException(ErrorCode.WRONG_ACCESS_EXCEPTION);
        }

        Report report = reportRepository.findById(approvalDto.reportId()).orElseThrow(
                () -> new ApplicationException(ErrorCode.NOT_FOUND_REPORT_EXCEPTION)
        );

        report.setApproval(approvalDto.approval());

        if(report.getReviewType() == ReviewType.PlanReview){
            PlanReview planReview = planReviewRepository.findById(report.getReview_id()).orElseThrow(
                    () -> new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION)
            );
            planReview.setReport(approvalDto.approval());

        }else if(report.getReviewType() == ReviewType.PlaceReview){
            PlaceReview placeReview = placeReviewRepository.findById(report.getReview_id()).orElseThrow(
                    () -> new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION)
            );
            placeReview.setReport(approvalDto.approval());
        }else {
            throw new ApplicationException(ErrorCode.REVIEW_TYPE_EXCEPTION);
        }
    }
}
