package Journey.Together.domain.place.controller;

import Journey.Together.domain.place.service.kakao.KakaoApiService;
import Journey.Together.domain.place.service.kakao.dto.KakaoAddress;
import Journey.Together.domain.place.service.kakao.dto.KakaoKeyword;
import Journey.Together.domain.place.service.match.TryMatchBatchService;
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
    private final KakaoApiService kakao;
    private final TryMatchBatchService service;

    @GetMapping("/address")
    public ApiResponse<KakaoAddress> getPlaceInfoByAddress(@RequestParam String address){
        return ApiResponse.success(Success.GET_PLACE_DETAIL_SUCCESS, kakao.getPlaceInfoByAddress(address,null));
    }

    @GetMapping("/keyword")
    public ApiResponse<KakaoKeyword> getPlaceInfoByKeyword(@RequestParam String keyword){
        return ApiResponse.success(Success.GET_PLACE_DETAIL_SUCCESS, kakao.getPlaceInfoByKeyword(keyword,null));
    }

    @PostMapping("/run")
    public TryMatchBatchService.RunResult run(
            @RequestParam(defaultValue = "200") int limit,
            @RequestParam(defaultValue = "true") boolean onlyActive
    ) {
        return service.run(limit, onlyActive);
    }
}
