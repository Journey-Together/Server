package Journey.Together.domain.place.controller;

import Journey.Together.domain.place.dto.request.PlaceReviewReq;
import Journey.Together.domain.place.dto.response.*;
import Journey.Together.domain.place.service.PlaceService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/place")
@Tag(name = "Place", description = "여행지 API")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/main")
    public ApiResponse<MainRes> getMain(
                                        @RequestParam String areacode, @RequestParam String sigungucode) {
        return ApiResponse.success(Success.GET_MAIN_SUCCESS, placeService.getMainPage(areacode, sigungucode));
    }

    @GetMapping("/{placeId}")
    public ApiResponse<PlaceDetailRes> getPlaceDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @PathVariable Long placeId){
        return ApiResponse.success(Success.GET_PLACE_DETAIL_SUCCESS, placeService.getPlaceDetail(principalDetails.getMember(), placeId));
    }

    @PostMapping("/review/{placeId}")
    public ApiResponse<?> createReivew(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                       @RequestPart(required = false) List<MultipartFile> images,
                                       @RequestPart("placeReviewReq") PlaceReviewReq placeReviewReq,
                                       @PathVariable Long placeId) {
        placeService.createReview(principalDetails.getMember(), images,placeReviewReq, placeId);
        return ApiResponse.success(Success.CREATE_PLACE_REVIEW_SUCCESS);
    }

    @GetMapping("/review/{placeId}")
    public ApiResponse<PlaceReviewRes> getPlaceReview(
            @PathVariable Long placeId, @PageableDefault(size = 5,page = 0) Pageable pageable) {
        return ApiResponse.success(Success.GET_PLACE_REVIEW_LIST_SUCCESS, placeService.getReviews(placeId, pageable));
    }

    @GetMapping("/review/my")
    public ApiResponse<MyPlaceReviewRes> getPlaceMyReview(
            @AuthenticationPrincipal PrincipalDetails principalDetails,@PageableDefault(size = 5,page = 0) Pageable pageable) {
        return ApiResponse.success(Success.GET_MY_PLACE_REVIEW_LIST_SUCCESS, placeService.getMyReviews(principalDetails.getMember(), pageable));
    }

}
