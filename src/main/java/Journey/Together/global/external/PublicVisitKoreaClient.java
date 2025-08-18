package Journey.Together.global.external;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Journey.Together.global.config.OpenFeignConfig;
import Journey.Together.global.external.dto.request.RequestBasicData;
import Journey.Together.global.external.dto.request.RequestCode;
import Journey.Together.global.external.dto.request.RequestDetailData;
import Journey.Together.global.external.dto.response.ResponseBasicData;
import Journey.Together.global.external.dto.response.ResponseCode;
import Journey.Together.global.external.dto.response.ResponseDataDetail;
import Journey.Together.global.external.dto.response.ResponsePlaceDisabilityCategory;
import Journey.Together.global.external.dto.response.ResponsePublicData;

@FeignClient(
	name = "public-data-client",
	url = "${public-data.tourism.base-url}",
	configuration = OpenFeignConfig.class)
public interface PublicVisitKoreaClient {

	/** 분류체계 코드 조회 **/
	@GetMapping("/lclsSystmCode2")
	ResponsePublicData<ResponseCode> fetchCategoryCode(@RequestParam Map<String, Object> params);


	/** 법정동 코드 조회 **/
	@GetMapping("/ldongCode2")
	ResponsePublicData<ResponseCode> fetchDongCode(@RequestParam Map<String, Object> params);

	/** 지역기반 관광정보 조회 **/
	@GetMapping("/areaBasedList2")
	ResponsePublicData<ResponseBasicData> fetchBasicData(@RequestParam Map<String, Object> params);

	/** 공통정보 조회 **/
	@GetMapping("/detailCommon2")
	ResponsePublicData<ResponseDataDetail> fetchDataDetail(@RequestParam Map<String, Object> params);

	/** 무장애정보 조회 **/
	@GetMapping("/detailWithTour2")
	ResponsePublicData<ResponsePlaceDisabilityCategory> fetchDisabilityCategoryData(@RequestParam Map<String, Object> params);

	/** 무장애 여행정보 동기화 목록 조회*/
	@GetMapping("/areaBasedSyncList2")
	ResponsePublicData<ResponseBasicData> fetchSyncData(@RequestParam Map<String, Object> params);

}
