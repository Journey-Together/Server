package Journey.Together.domain.placeReview.controller;

import Journey.Together.domain.place.dto.response.MainRes;
import Journey.Together.domain.placeReview.service.PlaceReviewService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/place")
@Tag(name = "PlaceReview", description = "여행지 후기 API")
public class PlaceReviewController {

    private final PlaceReviewService placeReviewService;

}
