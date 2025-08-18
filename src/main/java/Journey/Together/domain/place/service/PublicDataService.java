package Journey.Together.domain.place.service;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import Journey.Together.global.common.DiscordMessageSender;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.external.dto.FieldWithIndex;
import Journey.Together.global.external.dto.response.ResponseBasicData;
import Journey.Together.global.external.dto.response.ResponseDataDetail;
import Journey.Together.global.external.dto.response.ResponsePlaceDisabilityCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicDataService {
	private final DisabilityPlaceCategoryRepository disabilityPlaceCategoryRepository;
	private final DisabilitySubCategoryRepository disabilitySubCategoryRepository;
	private static final int BATCH_SIZE = 500;
	private static final String BLANK = " ";

	private final PlaceRepository placeRepository;
	private final PublicDataFetchService publicDataFetchService;
	private final DiscordMessageSender discordMessageSender;

	/** 공공데이터 저장 전체로 */
	@Transactional
	public void savePublicData(Member member){
		validate(member);

		savePublicDataBasic();
		savePublicDataDetail();
		savePublicDataCategory();
	}

	/** 공공데이터 기본정보 저장 */
	@Transactional
	public void savePublicDataBasic() {

		try {
			notifyPublicDataSaveStatus("공공데이터 *기본정보*  저장", "프로세스 시작");
			val categoryCodes = publicDataFetchService.getCategoryCodes();
			val dongCodes = publicDataFetchService.getDongCodes();

			List<ResponseBasicData> basicDataList = publicDataFetchService.fetchAllBasicData(categoryCodes, dongCodes);
			List<Place> placeList = basicDataList.stream()
				.map(ResponseBasicData::mapToEntity)
				.collect(Collectors.toList());

			placeRepository.saveAll(placeList);

			// notifyPublicDataSaveStatus("공공데이터 기본정보 저장", "공공데이터 기본정보 저장 프로세스 완료. 총 " + placeList.size() + "개의 장소 저장됨");

		} catch (Exception e) {
			log.error("공공데이터 기본정보 저장 중 오류 발생: {}", e.getMessage(), e);
			throw e;
		}
	}

	/** 공공데이터 상세정보(overview, homgepage) 저장 */
	@Transactional
	public void savePublicDataDetail() {

		try {
			notifyPublicDataSaveStatus("공공데이터 *상세정보* 저장", "프로세스 시작");
			int page = 0;

			while (true) {
				Pageable pageable = PageRequest.of(page, BATCH_SIZE);
				Page<Place> placePage = placeRepository.findAll(pageable);

				List<Place> batch = placePage.getContent();
				if (batch.isEmpty())
					break;

				List<Place> placeList = buildPlaceList(batch);

				placeRepository.saveAll(placeList);

				// notifyPublicDataSaveStatus("공공데이터 상세정보 저장", "batch " + page + " 저장 완료. 저장된 장소 수: " + placeList.size());

				if (!placePage.hasNext())
					break;
				page++;
			}

		} catch (Exception e) {
			log.error("공공데이터 상세정보 저장 중 오류 발생: {}", e.getMessage(), e);
			throw e;
		}
	}

	/** 공공데이터 카테고리 저장 */
	@Transactional
	public void savePublicDataCategory() {

		try {
			notifyPublicDataSaveStatus("공공데이터 *카테고리* 저장", "프로세스 시작");
			int page = 0;

			while (true) {
				Pageable pageable = PageRequest.of(page, BATCH_SIZE);
				Page<Place> placePage = placeRepository.findAll(pageable);

				List<Place> batch = placePage.getContent();
				if (batch.isEmpty())
					break;

				List<DisabilityPlaceCategory> disabilityPlaceCategories = buildPlaceCategoryList(batch);

				disabilityPlaceCategoryRepository.saveAll(disabilityPlaceCategories);
				placeRepository.saveAll(batch);

				// notifyPublicDataSaveStatus("공공데이터 카테고리 저장", "batch " + page + " 저장 완료. 저장된 장소 수: " + disabilityPlaceCategories.size());

				if (!placePage.hasNext())
					break;
				page++;
			}

		} catch (Exception e) {
			log.error("공공데이터 카테고리 저장 중 오류 발생: {}", e.getMessage(), e);
			throw e;
		}

		notifyPublicDataSaveStatus("공공데이터 저장", "공공데이터 저장 완료");
	}

	@Transactional
	public void syncAllData() {
		List<Place> places = getSyncBasicData();
		setSyncDataDetail(places);

		placeRepository.saveAll(places);

		notifyPublicDataSaveStatus("공공데이터 동기화", places.size() + "건 동기화 완료");
	}

	/** 동기화 데이터 */
	public List<Place> getSyncBasicData() {
		try {
			notifyPublicDataSaveStatus(" 공공데이터 기본정보 동기화 ", "프로세스 시작");

			List<ResponseBasicData> syncData = publicDataFetchService.fetchSyncData(LocalDate.now().minusDays(1));
			List<Place> placeList = syncData.stream()
				.map(ResponseBasicData::mapToEntity)
				.collect(Collectors.toList());

			return placeList;
		} catch (Exception e) {
			log.error("공공데이터 기본정보 동기화 중 오류 발생: {}", e.getMessage(), e);
			throw e;
		}
	}

	public void setSyncDataDetail(List<Place> syncPlaces) {
		try {
			notifyPublicDataSaveStatus("공공데이터 *상세정보* 동기화", "프로세스 시작");

			buildPlaceList(syncPlaces);

		} catch (Exception e) {
			log.error("공공데이터 상세정보 동기화 중 오류 발생: {}", e.getMessage(), e);
			throw e;
		}
	}

	private void validate(Member member) {
		if (!member.getMemberType().equals(MemberType.ADMIN)) {
			throw new ApplicationException(ErrorCode.UNAUTHORIZED_EXCEPTION);
		}
	}

	public List<Place> buildPlaceList(List<Place> places) {
		places.forEach(place -> {
			ResponseDataDetail detail = publicDataFetchService.getDetailData(place.getId())
				.stream()
				.findFirst()
				.orElse(null);
			place.setDetailData(detail != null ? extractHttpsUrl(detail.homepage()) : BLANK,
				detail != null ? detail.overview() : BLANK);
		});

		return places;
	}

	private List<DisabilityPlaceCategory> buildPlaceCategoryList(List<Place> places) {
		List<DisabilitySubCategory> subCategories = disabilitySubCategoryRepository.findAll();
		List<DisabilityPlaceCategory> categories = places.stream()
			.map(place -> {
				ResponsePlaceDisabilityCategory response = publicDataFetchService
					.getDisabilityCategories(place.getId())
					.stream()
					.findFirst()
					.orElse(null);

				Set<DisabilityPlaceCategory> placeCategories = mapToDisabilityPlaceCategoryEntity(place, response,
					subCategories);
				place.setDisabilityCategories(placeCategories);
				return new ArrayList<>(placeCategories);
			})
			.flatMap(List::stream)
			.toList();

		return categories;
	}

	private Set<DisabilityPlaceCategory> mapToDisabilityPlaceCategoryEntity(Place place,
		ResponsePlaceDisabilityCategory response, List<DisabilitySubCategory> subCategories) {
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

	private String extractHttpsUrl(String input) {
		if (input == null || input.isBlank()) {
			return "";
		}
		Pattern pattern = Pattern.compile("(https://[^\"'\\s>]+)");
		Matcher matcher = pattern.matcher(input);

		return matcher.find() ? matcher.group(1) : "";
	}

	private void notifyPublicDataSaveStatus(String title, String description) {
		discordMessageSender.sendDiscordAlarm("공공데이터 저장", title, description);
	}

}
