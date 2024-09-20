package Journey.Together.domain.report.dto;

import Journey.Together.domain.report.enumerate.ReviewType;

import java.util.List;

public record ReviewDto(
        Long reviewId,
        Long memberId,
        String memberName,
        ReviewType reviewType,
        String content,
        List<String> imgList

) {}

