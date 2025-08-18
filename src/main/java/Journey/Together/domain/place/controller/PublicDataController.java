package Journey.Together.domain.place.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Journey.Together.domain.place.service.PublicDataService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/internal/public-data")
@Tag(name = "Place", description = "여행지 API")
public class PublicDataController {

	private final PublicDataService publicDataService;

	/** 개별 저장 api - 필요없어서 주석처리
	 * 추후에 특정 분류만 필요할때 사용
	@PostMapping("/basic")
	public ApiResponse<?> savePublicDataBasic(@AuthenticationPrincipal PrincipalDetails principalDetails){
		publicDataService.savePublicDataBasic(principalDetails.getMember());
		return ApiResponse.success(Success.CREATE_SUCCESS);
	}

	@PostMapping("/detail")
	public ApiResponse<?> savePublicDataDetail(@AuthenticationPrincipal PrincipalDetails principalDetails){
		publicDataService.savePublicDataDetail(principalDetails.getMember());
		return ApiResponse.success(Success.CREATE_SUCCESS);
	}

	@PostMapping("/category")
	public ApiResponse<?> savePublicDataCategory(@AuthenticationPrincipal PrincipalDetails principalDetails){
		publicDataService.savePublicDataCategory(principalDetails.getMember());
		return ApiResponse.success(Success.CREATE_SUCCESS);
	}
	 */

	@PostMapping()
	public ApiResponse<?> savePublicData(@AuthenticationPrincipal PrincipalDetails principalDetails){
		publicDataService.savePublicData(principalDetails.getMember());
		return ApiResponse.success(Success.CREATE_SUCCESS);
	}
}
