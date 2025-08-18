package Journey.Together.domain.place.controller;

import Journey.Together.domain.place.service.kakao.PlaceSearchClient;
import Journey.Together.domain.place.service.kakao.dto.KakaoAddress;
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

    @GetMapping("")
    public ApiResponse<KakaoAddress> getPlaceAddressInfo(@RequestParam String searchAddress){
        return ApiResponse.success(Success.GET_PLACE_DETAIL_SUCCESS, placeSearchClient.getPlaceInfo(searchAddress,null));
    }
}
