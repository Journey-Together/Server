package Journey.Together.domain.place.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import Journey.Together.domain.place.repository.DisabilitySubCategoryRepository;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.global.external.PublicVisitKoreaClient;
import Journey.Together.global.external.dto.request.RequestBasicData;
import Journey.Together.global.external.dto.request.RequestCode;
import Journey.Together.global.external.dto.request.RequestDetailData;
import Journey.Together.global.external.dto.request.RequestSyncData;
import Journey.Together.global.external.dto.response.ResponseBasicData;
import Journey.Together.global.external.dto.response.ResponseCode;
import Journey.Together.global.external.dto.response.ResponseDataDetail;
import Journey.Together.global.external.dto.response.ResponsePlaceDisabilityCategory;
import Journey.Together.global.external.dto.response.ResponsePublicData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공공데이터 api에서 데이터를 받아오는 service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PublicDataFetchService {

	@Value("${public-data.tourism.service-key}")
	private String serviceKey;

	private static final int NUM_OF_ROWS = 100;
	private static final int INITIAL_PAGE_NO = 1;

	private final PublicVisitKoreaClient publicVisitKoreaClient;

	public List<String> getCategoryCodes() {
		try {
			ResponsePublicData<ResponseCode> categoryCodes = publicVisitKoreaClient.fetchCategoryCode(
				RequestCode.of(serviceKey, NUM_OF_ROWS, INITIAL_PAGE_NO).toMap());
			return extractItems(categoryCodes).stream()
				.map(ResponseCode::code).toList();
		} catch (Exception e) {
			log.error("분류체계 코드 데이터 호출 실패", e);
			return Collections.emptyList();
		}
	}

	public List<String> getDongCodes() {
		try {
			ResponsePublicData<ResponseCode> dongCodes = publicVisitKoreaClient.fetchDongCode(
				RequestCode.of(serviceKey, NUM_OF_ROWS, INITIAL_PAGE_NO).toMap());
			return extractItems(dongCodes).stream()
				.map(ResponseCode::code).toList();
		} catch (Exception e) {
			log.error("법정동 코드 데이터 호출 실패", e);
			return Collections.emptyList();
		}
	}


	/** 파라미터 필수 사항에 분류체계 코드(categoryCode)와 법정동코드(dongCode)가 포함되어있어
	 * 해당 code들을 불러와 이중 for문으로 모든 데이터를 fetch **/
	public List<ResponseBasicData> fetchAllBasicData(List<String> categoryCodes, List<String> dongCodes) {
		List<ResponseBasicData> allData = new ArrayList<>();

		for (String categoryCode : categoryCodes) {
			for (String dongCode : dongCodes) {
				List<ResponseBasicData> data = fetchBasicDataForDongAndCategory(dongCode, categoryCode);
				allData.addAll(data);
			}
		}
		return allData;
	}

	/** NUM_OF_ROWS 개씩 pageNo를 늘려가며 데이터가 더 이상 없을때까지 fetch **/
	private List<ResponseBasicData> fetchBasicDataForDongAndCategory(String dongCode, String categoryCode) {
		int pageNo = INITIAL_PAGE_NO;
		List<ResponseBasicData> dataList = new ArrayList<>();

		while (true) {
			List<ResponseBasicData> pageData = getBasicData(pageNo++, dongCode, categoryCode);
			if (pageData.isEmpty()) {
				break;
			}
			dataList.addAll(pageData);
		}
		return dataList;
	}

	/** 동기화 데이터
	 * NUM_OF_ROWS 개씩 pageNo를 늘려가며 데이터가 더 이상 없을때까지 fetch **/
	public List<ResponseBasicData> fetchSyncData(LocalDate modifiedTime) {
		int pageNo = INITIAL_PAGE_NO;
		List<ResponseBasicData> dataList = new ArrayList<>();

		while (true) {
			List<ResponseBasicData> pageData = getSyncData(pageNo++, modifiedTime);
			if (pageData.isEmpty()) {
				break;
			}
			dataList.addAll(pageData);
		}
		return dataList;
	}

	private List<ResponseBasicData> getBasicData(int pageNo, String dongCode, String categoryCode) {
		try {
			ResponsePublicData<ResponseBasicData> response = publicVisitKoreaClient.fetchBasicData(
				RequestBasicData.of(dongCode, categoryCode, serviceKey, NUM_OF_ROWS, pageNo).toMap());
			return extractItems(response);
		} catch (Exception e) {
			log.error("[" + dongCode + "][" + categoryCode + "] no." + pageNo + " 기본 관광 데이터 호출 실패", e);
			return Collections.emptyList();
		}
	}

	public List<ResponseDataDetail> getDetailData(Long contendId) {
		try {
			ResponsePublicData<ResponseDataDetail> response = publicVisitKoreaClient.fetchDataDetail(
				RequestDetailData.of(serviceKey, contendId).toMap());
			return extractItems(response);
		} catch (Exception e) {
			log.error("[" + contendId + "] 상세 관광 데이터 호출 실패", e);
			return Collections.emptyList();
		}
	}

	public List<ResponsePlaceDisabilityCategory> getDisabilityCategories(Long contendId) {
		try {
			ResponsePublicData<ResponsePlaceDisabilityCategory> response = publicVisitKoreaClient.fetchDisabilityCategoryData(
				RequestDetailData.of(serviceKey, contendId).toMap());
			return extractItems(response);
		} catch (Exception e) {
			log.error("[" + contendId + "] 관광 장애 카테고리 데이터 호출 실패", e);
			return Collections.emptyList();
		}
	}

	public List<ResponseBasicData> getSyncData(int pageNo, LocalDate modifiedTime) {
		try {
			ResponsePublicData<ResponseBasicData> response = publicVisitKoreaClient.fetchBasicData(
				RequestSyncData.of(modifiedTime, serviceKey, NUM_OF_ROWS, pageNo).toMap());
			return extractItems(response);
		} catch (Exception e) {
			log.error("[" + modifiedTime + "] no." + pageNo + " 동기화 관광 데이터 호출 실패", e);
			return Collections.emptyList();
		}
	}

	private <T> List<T> extractItems(ResponsePublicData<T> response) {
		return Optional.ofNullable(response)
			.map(ResponsePublicData::getResponse)
			.map(ResponsePublicData.Response::getBody)
			.map(ResponsePublicData.Response.Body::getItems)
			.map(ResponsePublicData.Response.Body.Items::getItem)
			.orElse(Collections.emptyList());
	}
}
