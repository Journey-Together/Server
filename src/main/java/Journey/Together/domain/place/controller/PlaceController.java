package Journey.Together.domain.place.controller;

import Journey.Together.domain.place.dto.request.PlaceReviewReq;
import Journey.Together.domain.place.dto.response.*;
import Journey.Together.domain.member.dto.LoginReq;
import Journey.Together.domain.member.dto.MemberRes;
import Journey.Together.domain.member.service.MemberService;
import Journey.Together.domain.place.dto.response.MainRes;
import Journey.Together.domain.place.dto.response.PlaceDetailRes;
import Journey.Together.domain.place.dto.response.PlaceRes;
import Journey.Together.domain.place.dto.response.SearchPlaceRes;
import Journey.Together.domain.place.service.PlaceService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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
        if (images == null) {
            images = new ArrayList<>(); // images가 null이면 빈 리스트로 초기화
        }
        placeService.createReview(principalDetails.getMember(), images,placeReviewReq, placeId);
        return ApiResponse.success(Success.CREATE_PLACE_REVIEW_SUCCESS);
    }

    @GetMapping("/review/{placeId}")
    public ApiResponse<PlaceReviewRes> getPlaceReview(
            @PathVariable Long placeId, @PageableDefault(size = 5,page = 0) Pageable pageable) {
        return ApiResponse.success(Success.GET_PLACE_REVIEW_LIST_SUCCESS, placeService.getReviews(placeId, pageable));
    }

    @GetMapping("/review/my")
    public ApiResponse<MyPlaceReviewRes> getPlaceMyReviews(
            @AuthenticationPrincipal PrincipalDetails principalDetails,@PageableDefault(size = 5,page = 0) Pageable pageable) {
        return ApiResponse.success(Success.GET_MY_PLACE_REVIEW_LIST_SUCCESS, placeService.getMyReviews(principalDetails.getMember(), pageable));
    }

    @DeleteMapping("/review/my/{reviewId}")
    public ApiResponse<?> deletePlaceMyReview(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long reviewId) {
        placeService.deleteMyPlaceReview(principalDetails.getMember(),reviewId);
        return ApiResponse.success(Success.DELETE_MY_PLACE_REVIEW_SUCCESS);
    }

    @GetMapping("/review/my/{reviewId}")
    public ApiResponse<MyReview> getPlaceMyReview(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long reviewId) {
        return ApiResponse.success(Success.GET_MY_PLACE_REVIEW_SUCCESS,placeService.getReview(principalDetails.getMember(),reviewId));
    }
    @GetMapping("/search/list")
    public ApiResponse<SearchPlaceRes> searchPlaceList(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @RequestParam @NotNull String category,
                                                       @RequestParam @NotNull String query,
                                                       @RequestParam(required = false) Double minX,
                                                       @RequestParam(required = false) Double maxX,
                                                       @RequestParam(required = false) Double minY,
                                                       @RequestParam(required = false) Double maxY,
                                                       @RequestParam(required = false)List<Long> disabilityType, @RequestParam(required = false) List<Long> detailFilter,
                                                       @RequestParam(required = false) String areacode,
                                                       @RequestParam(required = false) String sigungucode,
                                                       @RequestParam(required = false) String arrange,
                                                       @PageableDefault(size = 10,page = 0) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_PLACE_LIST_SUCCESS, placeService.searchPlaceList(category,query,disabilityType,detailFilter,areacode,sigungucode,arrange,pageable,
                minX,maxX,minY,maxY));
    }

}
