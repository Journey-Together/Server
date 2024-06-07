package Journey.Together.domain.bookbark.controller;

import Journey.Together.domain.bookbark.dto.PlaceBookmarkRes;
import Journey.Together.domain.bookbark.service.BookmarkService;
import Journey.Together.domain.place.dto.response.PlaceBookmarkDto;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/bookmark")
@Tag(name = "Bookmark", description = "북마크 API")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/names")
    public ApiResponse<List<PlaceBookmarkDto>> getBookmarkPlaceNames(
            @AuthenticationPrincipal PrincipalDetails principalDetails){
        return ApiResponse.success(Success.GET_BOOKMARK_PLACE_NAMES_SUCCESS, bookmarkService.getBookmarkPlaceNames(principalDetails.getMember()));
    }
    @GetMapping("/place")
    public ApiResponse<List<PlaceBookmarkRes>> getBookmarks(
            @AuthenticationPrincipal PrincipalDetails principalDetails){
        return ApiResponse.success(Success.GET_BOOKMARK_PLACES_SUCCESS, bookmarkService.getPlaceBookmarks(principalDetails.getMember()));
    }


    @PatchMapping("/{placeId}")
    public ApiResponse<List<PlaceBookmarkDto>> getBookmarkPlaceNames(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long placeId){
        bookmarkService.bookmark(principalDetails.getMember(), placeId);
        return ApiResponse.success(Success.CHANGE_BOOKMARK_SUCCESS);
    }

}
