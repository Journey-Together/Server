package Journey.Together.domain.place.service;

import Journey.Together.domain.dairy.entity.PlanReviewImage;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.dto.request.PlaceReviewReq;
import Journey.Together.domain.place.dto.response.MainRes;
import Journey.Together.domain.place.dto.response.PlaceDetailRes;
import Journey.Together.domain.place.dto.response.PlaceRes;
import Journey.Together.domain.place.dto.response.PlaceReviewDto;
import Journey.Together.domain.place.entity.DisabilityPlaceCategory;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.DisabilityPlaceCategoryRepository;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.domain.placeBookbark.entity.PlaceBookmark;
import Journey.Together.domain.placeBookbark.repository.PlaceBookmarkRepository;
import Journey.Together.domain.placeReview.entity.PlaceReview;
import Journey.Together.domain.placeReview.entity.PlaceReviewImg;
import Journey.Together.domain.placeReview.repository.PlaceReviewImgRepository;
import Journey.Together.domain.placeReview.repository.PlaceReviewRepository;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.exception.ErrorResponse;
import Journey.Together.global.exception.Success;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import Journey.Together.global.util.S3Client;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceReviewRepository placeReviewRepository;
    private final PlaceReviewImgRepository placeReviewImgRepository;
    private final DisabilityPlaceCategoryRepository disabilityPlaceCategoryRepository;
    private final PlaceBookmarkRepository placeBookmarkRepository;

    private final S3Client s3Client;

    private static final String POST_IMAGE_FOLDER_NAME = "reviews/";
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

    //여행지 후기 생성
    @Transactional
    public void createReview(Member member, List<MultipartFile> images, PlaceReviewReq placeReviewReq, Long placeId){

        Place place = placeRepository.findById(placeId).orElseThrow(
                ()->new ApplicationException(ErrorCode.NOT_FOUND_PLACE_EXCEPTION));

        if(placeReviewRepository.findPlaceReviewByPlace(place) != null)
            new ApplicationException(ErrorCode.ALREADY_EXIST_EXCEPTION);

        PlaceReview placeReview = PlaceReview.builder()
                .member(member)
                .place(place)
                .content(placeReviewReq.content())
                .grade(placeReviewReq.grade())
                .date(placeReviewReq.date())
                .build();

        placeReviewRepository.save(placeReview);

        try {
            for(MultipartFile file : images) {
                String uuid = UUID.randomUUID().toString();
                final String imageUrl = s3Client.upload(file, POST_IMAGE_FOLDER_NAME+member.getMemberId(), uuid);
                PlaceReviewImg placeReviewImg = PlaceReviewImg.builder().placeReview(placeReview).imgUrl(imageUrl).build();
                placeReviewImgRepository.save(placeReviewImg);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }


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
