package Journey.Together.domain.plan.controller;

import Journey.Together.domain.place.service.query.PlaceQueryService;
import Journey.Together.domain.plan.dto.PlaceInfoPageRes;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plan")
@Tag(name = "PlanPlace", description = "일정 장소 관련 API")
public class PlaceQueryController {
    private final PlaceQueryService placeQueryService;

    @GetMapping("/search")
    public ApiResponse<PlaceInfoPageRes> searchPlace(@RequestParam String word, @PageableDefault(size = 6,page = 0) Pageable pageable){
        return ApiResponse.success(Success.SEARCH_SUCCESS,placeQueryService.searchPlace(word,pageable));
    }
}
