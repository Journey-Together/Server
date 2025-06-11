package Journey.Together.domain.place.service;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.enumerate.MemberType;
import Journey.Together.domain.place.entity.DisabilityPlaceCategory;
import Journey.Together.domain.place.entity.DisabilitySubCategory;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.DisabilityPlaceCategoryRepository;
import Journey.Together.domain.place.repository.DisabilitySubCategoryRepository;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.external.PublicVisitKoreaClient;
import Journey.Together.global.external.dto.FieldWithIndex;
import Journey.Together.global.external.dto.request.RequestBasicData;
import Journey.Together.global.external.dto.request.RequestCode;
import Journey.Together.global.external.dto.request.RequestDetailData;
import Journey.Together.global.external.dto.response.ResponseBasicData;
import Journey.Together.global.external.dto.response.ResponseCode;
import Journey.Together.global.external.dto.response.ResponseDataDetail;
import Journey.Together.global.external.dto.response.ResponsePlaceDisabilityCategory;
import Journey.Together.global.external.dto.response.ResponsePublicData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PublicDataService {

	@Value("${publicdata.tourism.service-key}")
	private String serviceKey;

	private static final int NUM_OF_ROWS = 100;
	private static final int INITIAL_PAGE_NO = 1;

	private final PublicVisitKoreaClient publicVisitKoreaClient;
	private final PlaceRepository placeRepository;
	private final DisabilitySubCategoryRepository disabilitySubCategoryRepository;

	public void savePublicData(Member member) {
		validate(member);

		try {
			log.info("공공데이터 저장 프로세스 시작");
			val categoryCodes = getCategoryCodes();
			val dongCodes = getDongCodes();

			List<ResponseBasicData> basicDataList = fetchAllBasicData(categoryCodes, dongCodes);
			List<Place> placeList = buildPlaceList(basicDataList);

			placeRepository.saveAll(placeList);

			log.info("공공데이터 저장 프로세스 완료. 총 {}개의 장소 저장됨", placeList.size());

		} catch (Exception e) {
			log.error("공공데이터 저장 중 오류 발생: {}", e.getMessage(), e);
			notifyError("공공데이터 저장 중 오류 발생", e);
			throw e;
		}
	}

	private void validate(Member member){
		if(!member.getMemberType().equals(MemberType.ADMIN)){
			throw new ApplicationException(ErrorCode.UNAUTHORIZED_EXCEPTION);
		}
	}

	/** 파라미터 필수 사항에 분류체계 코드(categoryCode)와 법정동코드(dongCode)가 포함되어있어
	 * 해당 code들을 불러와 이중 for문으로 모든 데이터를 fetch **/
	private List<ResponseBasicData> fetchAllBasicData(List<String> categoryCodes, List<String> dongCodes) {
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

	/** 각 장소별 상세 정보 **/
	private List<Place> buildPlaceList(List<ResponseBasicData> basicDataList) {
		List<Place> places = new ArrayList<>();

		basicDataList.parallelStream().forEach(basic -> {
			ResponseDataDetail detail = getDetailData(basic.contentid()).stream().findFirst().orElse(null);
			ResponsePlaceDisabilityCategory placeDisabilityCategories = getDisabilityCategories(
				basic.contentid()).stream().findFirst().orElse(null);

			Place newPlace = mapToEntity(basic, detail);
			newPlace.setDisabilityCategories(
				mapToDisabilityPlaceCategoryEntity(newPlace, placeDisabilityCategories));
		});

		return places;
	}

	public List<String> getCategoryCodes() {
		try {
			ResponsePublicData<ResponseCode> categoryCodes = publicVisitKoreaClient.fetchCategoryCode(
				RequestCode.of(serviceKey, NUM_OF_ROWS, INITIAL_PAGE_NO));
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
				RequestCode.of(serviceKey, NUM_OF_ROWS, INITIAL_PAGE_NO));
			return extractItems(dongCodes).stream()
				.map(ResponseCode::code).toList();
		} catch (Exception e) {
			log.error("법정동 코드 데이터 호출 실패", e);
			return Collections.emptyList();
		}
	}

	public List<ResponseBasicData> getBasicData(int pageNo, String dongCode, String categoryCode) {
		try {
			ResponsePublicData<ResponseBasicData> response = publicVisitKoreaClient.fetchBasicData(
				RequestBasicData.of(dongCode, categoryCode, serviceKey, NUM_OF_ROWS, pageNo));
			return extractItems(response);
		} catch (Exception e) {
			log.error("기본 관광 데이터 호출 실패", e);
			return Collections.emptyList();
		}
	}

	public List<ResponseDataDetail> getDetailData(Long contendId) {
		try {
			ResponsePublicData<ResponseDataDetail> response = publicVisitKoreaClient.fetchDataDetail(
				RequestDetailData.of(serviceKey, contendId));
			return extractItems(response);
		} catch (Exception e) {
			log.error("상세 관광 데이터 호출 실패", e);
			return Collections.emptyList();
		}
	}

	public List<ResponsePlaceDisabilityCategory> getDisabilityCategories(Long contendId) {
		try {
			ResponsePublicData<ResponsePlaceDisabilityCategory> response = publicVisitKoreaClient.fetchDisabilityCategoryData(
				RequestDetailData.of(serviceKey, contendId));
			return extractItems(response);
		} catch (Exception e) {
			log.error("관광 장애 카테고리 데이터 호출 실패", e);
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

	private Set<DisabilityPlaceCategory> mapToDisabilityPlaceCategoryEntity(Place place,
		ResponsePlaceDisabilityCategory response) {
		List<DisabilitySubCategory> subCategories = disabilitySubCategoryRepository.findAll();
		return extractFields(response).stream()
			.filter(it -> !it.value().isBlank())
			.map(it -> new DisabilityPlaceCategory(place, subCategories.get(it.index()), it.value())).collect(
				Collectors.toSet());
	}

	private List<FieldWithIndex> extractFields(ResponsePlaceDisabilityCategory response) {
		RecordComponent[] components = response.getClass().getRecordComponents();
		List<FieldWithIndex> result = new ArrayList<>();

		int index = 0;
		for (RecordComponent component : components) {
			try {
				Method accessor = component.getAccessor();
				Object value = accessor.invoke(response);

				if (value instanceof String str && !str.isBlank()) {
					result.add(new FieldWithIndex(index, component.getName(), str));
				}

				index++;

			} catch (Exception e) {
				throw new RuntimeException("필드 접근 실패: " + component.getName(), e);
			}
		}

		return result;
	}

	private Place mapToEntity(ResponseBasicData basic, ResponseDataDetail detail) {
		return new Place(
			basic.contentid(),
			basic.title(),
			basic.addr1() + " " + basic.addr2(),
			basic.firstimage(),
			basic.cat1(),
			basic.mapx(),
			basic.mapy(),
			basic.createdtime(),
			detail != null ? detail.overview() : null,
			basic.areacode(),
			basic.sigungucode(),
			basic.tel(),
			detail != null ? extractHttpsUrl(detail.homepage()) : null
		);
	}

	private String extractHttpsUrl(String input) {
		if (input == null || input.isBlank()) {
			return "";
		}
		Pattern pattern = Pattern.compile("(https://[^\"'\\s>]+)");
		Matcher matcher = pattern.matcher(input);

		return matcher.find() ? matcher.group(1) : "";
	}

	private void notifyError(String context, Exception e) {
		//todo 디스코드 알람 연동
		log.error("오류 알림 전송 - context: {}", context, e);
	}
}
