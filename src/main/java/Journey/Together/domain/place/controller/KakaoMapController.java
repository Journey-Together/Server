package Journey.Together.domain.place.controller;

import Journey.Together.domain.place.service.kakao.PlaceSearchClient;
import Journey.Together.domain.place.service.kakao.dto.KakaoAddress;
import Journey.Together.domain.place.service.kakao.dto.KakaoKeyword;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/place/kakao-map")
@Tag(name = "KakaoMap", description = "Kakao 지도 API")
public class KakaoMapController {
    private final PlaceSearchClient placeSearchClient;

    @GetMapping("/address")
    public ApiResponse<KakaoAddress> getPlaceInfoByAddress(@RequestParam String address){
        return ApiResponse.success(Success.GET_PLACE_DETAIL_SUCCESS, placeSearchClient.getPlaceInfoByAddress(address,null));
    }

    @GetMapping("/keyword")
    public ApiResponse<KakaoKeyword> getPlaceInfoByKeyword(@RequestParam String keyword){
        return ApiResponse.success(Success.GET_PLACE_DETAIL_SUCCESS, placeSearchClient.getPlaceInfoByKeyWord(keyword,null));
    }
}
