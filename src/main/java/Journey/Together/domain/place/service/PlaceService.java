package Journey.Together.domain.place.service;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.dto.response.*;
import Journey.Together.domain.place.entity.DisabilityPlaceCategory;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.DisabilityPlaceCategoryRepository;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.domain.placeBookbark.entity.PlaceBookmark;
import Journey.Together.domain.placeBookbark.repository.PlaceBookmarkRepository;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.exception.ErrorResponse;

import org.springframework.data.domain.Pageable;
import Journey.Together.global.exception.Success;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final DisabilityPlaceCategoryRepository disabilityPlaceCategoryRepository;
    private final PlaceBookmarkRepository placeBookmarkRepository;


    private final Integer recommnedPlaceNum = 4;
    private final Integer aroundPlaceNum = 2;

    // 메인페이지 가져오기
    public MainRes getMainPage(String areacode, String sigungucode){

        List<Place> recommondPlaces = placeRepository.findRandomProducts(recommnedPlaceNum);
        List<Place> aroundPlaces = placeRepository.findAroundProducts(areacode, sigungucode, aroundPlaceNum);


        return new MainRes(getPlaceRes(recommondPlaces), getPlaceRes(aroundPlaces));
    }

    //여행지 상세 정보 가져오기
    public PlaceDetailRes getPlaceDetail(Member member, Long placeId){
       // PlaceDetailRes of(Place place, Boolean isMark, Integer bookmarkNum, List<String> disability, List<String> subDisability, List< PlaceReviewDto > reviewList)

        Place place = placeRepository.findById(placeId).orElseThrow(
                ()->new ApplicationException(ErrorCode.NOT_FOUND_PLACE_EXCEPTION));

        List<PlaceBookmark> placeBookmarkList = placeBookmarkRepository.findAllByPlace(place);
        Boolean isMark = placeBookmarkList.stream()
                .anyMatch(placeBookmark -> placeBookmark.getMember().equals(member));

        List<Long> disability = disabilityPlaceCategoryRepository.findDisabilityCategoryIds(placeId);
        List<Long> subDisability = disabilityPlaceCategoryRepository.findDisabilitySubCategoryIds(placeId);

        return PlaceDetailRes.of(place, isMark, placeBookmarkList.size(), disability, subDisability, null);

    }

    public SearchPlaceRes searchPlaceList(String category, String query, List<Long> disabilityType, List<Long> detailFilter, String areacode, String sigungucode, String arrange,
                                          Pageable pageable, Double minX, Double maxX, Double minY, Double maxY){
        List<PlaceRes> placeResList =new ArrayList<>();

        SearchPlace searchPlace = placeRepository.search(category, query, disabilityType, detailFilter, areacode, sigungucode, arrange, pageable,
                minX, maxX, minY, maxY);
        searchPlace.places().forEach(
                place -> placeResList.add(PlaceRes.of(place,disabilityPlaceCategoryRepository.findDisabilityCategoryIds(place.getId())))
        );

        return new SearchPlaceRes(searchPlace.size(), placeResList);
    }

    private List<PlaceRes> getPlaceRes(List<Place> list){
        List<PlaceRes> placeList = new ArrayList<>();

        for(Place place : list){
            Set<Long> disability = new HashSet<>();
            disabilityPlaceCategoryRepository.findAllByPlace(place)
                    .forEach(disabilityPlaceCategory -> {
                        disability.add(disabilityPlaceCategory.getSubCategory().getCategory().getId());
                    });
            placeList.add(PlaceRes.of(place, new ArrayList<>(disability)));
        }

        return placeList;
    }


}
