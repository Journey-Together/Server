package Journey.Together.domain.place.controller;

import Journey.Together.domain.place.dto.request.PlaceReviewReq;
import Journey.Together.domain.place.dto.request.UpdateReviewDto;
import Journey.Together.domain.place.dto.response.*;
import Journey.Together.domain.member.service.MemberService;
import Journey.Together.domain.place.dto.response.MainRes;
import Journey.Together.domain.place.dto.response.PlaceDetailRes;
import Journey.Together.domain.place.dto.response.PlaceRes;
import Journey.Together.domain.place.dto.response.SearchPlaceRes;
import Journey.Together.domain.place.service.DataMigrationService;
import Journey.Together.domain.place.service.PlaceService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/place")
@Tag(name = "Place", description = "여행지 API")
public class PlaceController {

    private final PlaceService placeService;
    private final DataMigrationService dataMigrationService;

    @GetMapping("/main")
    public ApiResponse<MainRes> getMain(
                                        @RequestParam String areacode, @RequestParam String sigungucode) {
        return ApiResponse.success(Success.GET_MAIN_SUCCESS, placeService.getMainPage(areacode, sigungucode));
    }

    @GetMapping("/{placeId}")
    public ApiResponse<PlaceDetailRes> getPlaceDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @PathVariable Long placeId){
        return ApiResponse.success(Success.GET_PLACE_DETAIL_SUCCESS, placeService.getPlaceDetail(principalDetails.getMemberId(), placeId));
    }

    @GetMapping("guest/{placeId}")
    public ApiResponse<PlaceDetailGuestRes> getPlaceDetail(@PathVariable Long placeId){
        return ApiResponse.success(Success.GET_PLACE_DETAIL_SUCCESS, placeService.getGeustPlaceDetail(placeId));
    }

    @PostMapping("/review/{placeId}")
    public ApiResponse<?> createReivew(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                       @RequestPart(required = false) List<MultipartFile> images,
                                       @RequestPart PlaceReviewReq placeReviewReq,
                                       @PathVariable Long placeId) {
        images = (images == null) ? new ArrayList<>() : images;
        placeService.createReview(principalDetails.getMemberId(), images,placeReviewReq, placeId);
        return ApiResponse.success(Success.CREATE_PLACE_REVIEW_SUCCESS);
    }

    @GetMapping("/review/{placeId}")
    public ApiResponse<PlaceReviewRes> getPlaceReview(@AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long placeId, @PageableDefault(size = 5,page = 0) Pageable pageable) {
        return ApiResponse.success(Success.GET_PLACE_REVIEW_LIST_SUCCESS, placeService.getReviews(principalDetails.getMemberId(), placeId, pageable));
    }

    @GetMapping("/review/guest/{placeId}")
    public ApiResponse<PlaceReviewRes> getPlaceReview(
            @PathVariable Long placeId, @PageableDefault(size = 5,page = 0) Pageable pageable) {
        return ApiResponse.success(Success.GET_PLACE_REVIEW_LIST_SUCCESS, placeService.getReviewsGeust(placeId, pageable));
    }

    @GetMapping("/review/my")
    public ApiResponse<MyPlaceReviewRes> getPlaceMyReviews(
            @AuthenticationPrincipal PrincipalDetails principalDetails,@PageableDefault(size = 5,page = 0) Pageable pageable) {
        return ApiResponse.success(Success.GET_MY_PLACE_REVIEW_LIST_SUCCESS, placeService.getMyReviews(principalDetails.getMemberId(), pageable));
    }

    @DeleteMapping("/review/my/{reviewId}")
    public ApiResponse<?> deletePlaceMyReview(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long reviewId) {
        placeService.deleteMyPlaceReview(principalDetails.getMemberId(),reviewId);
        return ApiResponse.success(Success.DELETE_MY_PLACE_REVIEW_SUCCESS);
    }

    @GetMapping("/review/my/{reviewId}")
    public ApiResponse<MyReview> getPlaceMyReview(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long reviewId) {
        return ApiResponse.success(Success.GET_MY_PLACE_REVIEW_SUCCESS,placeService.getReview(principalDetails.getMemberId(),reviewId));
    }

    @PatchMapping("/review/my/{reviewId}")
    public ApiResponse<?> updatePlaceMyReview(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestPart(required = false) UpdateReviewDto updateReviewDto,
            @RequestPart(required = false) List<MultipartFile> addImages,
            @PathVariable Long reviewId) {
        addImages = (addImages == null) ? new ArrayList<>() : addImages;
        placeService.updateMyPlaceReview(principalDetails.getMemberId(),updateReviewDto,addImages,reviewId);
        return ApiResponse.success(Success.UPDATE_MY_PLACE_REVIEW_SUCCESS);
    }
    @GetMapping("/search")
    public ApiResponse<SearchPlaceRes> searchPlaceList(
                                                       @RequestParam(required = false) String category,
                                                       @RequestParam(required = false) String query,
                                                       @RequestParam(required = false) List<Long> disabilityType,
                                                       @RequestParam(required = false) List<Long> detailFilter,
                                                       @RequestParam(required = false) String areacode,
                                                       @RequestParam(required = false) String sigungucode,
                                                       @RequestParam(required = false) String arrange,
                                                       @PageableDefault(size = 10,page = 0) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_PLACE_LIST_SUCCESS, placeService.searchPlaceList(category,query,disabilityType,detailFilter,areacode,sigungucode,arrange,pageable));
    }

    @GetMapping("/search/map")
    public ApiResponse<List<PlaceRes>> searchPlaceList(
            @RequestParam(required = false) String category,
            @RequestParam @NotNull Double minX,
            @RequestParam @NotNull Double maxX,
            @RequestParam @NotNull Double minY,
            @RequestParam @NotNull Double maxY,
            @RequestParam(required = false)List<Long> disabilityType,
            @RequestParam(required = false) List<Long> detailFilter,
            @RequestParam(required = false) String arrange){
        return ApiResponse.success(Success.SEARCH_PLACE_LIST_SUCCESS, placeService.searchPlaceMap(category,disabilityType,detailFilter,arrange,minX,maxX,minY,maxY));
    }

    @GetMapping("/search/autocomplete")
    public ApiResponse<List<Map<String,Object>>> searchPlaceComplete(
            @RequestParam String query
    ) throws IOException {
        return ApiResponse.success(Success.SEARCH_COMPLETE_SUCCESS, placeService.searchPlaceComplete(query));
    }

    @GetMapping("/search/autocomplete/migration")
    public ApiResponse<?> migrationData(
    ) throws IOException {
        dataMigrationService.migrateData();
        return ApiResponse.success(Success.SEARCH_COMPLETE_SUCCESS);
    }

}
