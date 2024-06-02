package Journey.Together.domain.place.controller;

import Journey.Together.domain.member.dto.LoginReq;
import Journey.Together.domain.member.dto.MemberRes;
import Journey.Together.domain.member.service.MemberService;
import Journey.Together.domain.place.dto.response.MainRes;
import Journey.Together.domain.place.dto.response.PlaceDetailRes;
import Journey.Together.domain.place.service.PlaceService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/place")
@Tag(name = "Place", description = "여행지 정보 API")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/main")
    public ApiResponse<MainRes> getMain(@RequestHeader("Authorization") String accesstoken,
                                        @RequestParam String areacode, @RequestParam String sigungucode) {
        return ApiResponse.success(Success.GET_MAIN_SUCCESS, placeService.getMainPage(areacode, sigungucode));
    }

    @GetMapping("/{placeId}")
    public ApiResponse<PlaceDetailRes> getPlaceDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @PathVariable Long placeId){
        return ApiResponse.success(Success.GET_PLACE_DETAIL_SUCCESS, placeService.getPlaceDetail(principalDetails.getMember(), placeId));
    }

}