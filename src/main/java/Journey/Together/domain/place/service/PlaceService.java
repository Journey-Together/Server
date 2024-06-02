package Journey.Together.domain.place.service;

import Journey.Together.domain.place.dto.response.MainRes;
import Journey.Together.domain.place.dto.response.PlaceRes;
import Journey.Together.domain.place.entity.DisabilityPlaceCategory;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.DisabilityPlaceCategoryRepository;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final DisabilityPlaceCategoryRepository disabilityPlaceCategoryRepository;
    private final Integer recommnedPlaceNum = 4;
    private final Integer aroundPlaceNum = 2;

    // 메인페이지 가져오기
    public MainRes getMainPage(String areacode, String sigungucode){

        List<Place> recommondPlaces = placeRepository.findRandomProducts(recommnedPlaceNum);
        List<Place> aroundPlaces = placeRepository.findAroundProducts(areacode, sigungucode, aroundPlaceNum);


        return new MainRes(getPlaceRes(recommondPlaces), getPlaceRes(aroundPlaces));
    }

    private List<PlaceRes> getPlaceRes(List<Place> list){
        List<PlaceRes> placeList = new ArrayList<>();

        for(Place place : list){
            Set<String> disability = new HashSet<>();
            disabilityPlaceCategoryRepository.findAllByPlace(place)
                    .forEach(disabilityPlaceCategory -> {
                        disability.add(disabilityPlaceCategory.getSubCategory().getCategory().getId().toString());
                    });
            placeList.add(PlaceRes.of(place, new ArrayList<>(disability)));
        }

        return placeList;
    }

}
