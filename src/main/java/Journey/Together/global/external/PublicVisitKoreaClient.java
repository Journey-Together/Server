package Journey.Together.global.external;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Journey.Together.global.config.OpenFeignConfig;
import Journey.Together.global.external.dto.request.RequestBasicData;
import Journey.Together.global.external.dto.request.RequestCode;
import Journey.Together.global.external.dto.request.RequestDetailData;
import Journey.Together.global.external.dto.request.RequestDisabilityCategoryData;
import Journey.Together.global.external.dto.response.ResponseBasicData;
import Journey.Together.global.external.dto.response.ResponseCode;
import Journey.Together.global.external.dto.response.ResponseDataDetail;
import Journey.Together.global.external.dto.response.ResponsePlaceDisabilityCategory;
import Journey.Together.global.external.dto.response.ResponsePublicData;

@FeignClient(
	name = "public-data-client",
	url = "${publicdata.tourism.base-url}",
	configuration = OpenFeignConfig.class)
public interface PublicVisitKoreaClient {

	/** 분류체계 코드 조회 **/
	@GetMapping("/lclsSystmCode2")
	ResponsePublicData<ResponseCode> fetchCategoryCode(@RequestParam RequestCode request);


	/** 법정동 코드 조회 **/
	@GetMapping("/ldongCode2")
	ResponsePublicData<ResponseCode> fetchDongCode(@RequestParam RequestCode request);

	/** 지역기반 관광정보 조회 **/
	@GetMapping("/areaBasedList2")
	ResponsePublicData<ResponseBasicData> fetchBasicData(@RequestParam RequestBasicData request);

	/** 공통정보 조회 **/
	@GetMapping("/detailCommon2")
	ResponsePublicData<ResponseDataDetail> fetchDataDetail(@RequestParam RequestDetailData request);

	/** 무장애정보 조회 **/
	@GetMapping("/detailWithTour2")
	ResponsePublicData<ResponsePlaceDisabilityCategory> fetchDisabilityCategoryData(@RequestParam RequestDetailData request);
}
