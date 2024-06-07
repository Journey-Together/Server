package Journey.Together.domain.place.service;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.dto.request.PlaceReviewReq;
import Journey.Together.domain.place.dto.response.*;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.DisabilityPlaceCategoryRepository;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.domain.placeBookbark.entity.PlaceBookmark;
import Journey.Together.domain.placeBookbark.repository.PlaceBookmarkRepository;
import Journey.Together.domain.place.entity.PlaceReview;
import Journey.Together.domain.place.entity.PlaceReviewImg;
import Journey.Together.domain.place.repository.PlaceReviewImgRepository;
import Journey.Together.domain.place.repository.PlaceReviewRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import Journey.Together.global.util.S3Client;

import java.util.*;
import java.util.stream.Collectors;

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
    private final String partToFind = "com/";

    // 메인페이지 가져오기
    public MainRes getMainPage(String areacode, String sigungucode){

        List<Place> recommondPlaces = placeRepository.findRandomProducts(recommnedPlaceNum);
        List<Place> aroundPlaces = placeRepository.findAroundProducts(areacode, sigungucode, aroundPlaceNum);


        return new MainRes(getPlaceRes(recommondPlaces), getPlaceRes(aroundPlaces));
    }

    //여행지 상세 정보 가져오기
    public PlaceDetailRes getPlaceDetail(Member member, Long placeId){
       // PlaceDetailRes of(Place place, Boolean isMark, Integer bookmarkNum, List<String> disability, List<String> subDisability, List< PlaceReviewDto > reviewList)

        Place place = getPlace(placeId);

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
        Place place = getPlace(placeId);

        if(placeReviewRepository.findPlaceReviewByPlace(place) != null)
            throw new ApplicationException(ErrorCode.ALREADY_EXIST_EXCEPTION);

        PlaceReview placeReview = PlaceReview.builder()
                .member(member)
                .place(place)
                .content(placeReviewReq.content())
                .grade(placeReviewReq.grade())
                .date(placeReviewReq.date())
                .build();

        placeReviewRepository.save(placeReview);

        if(images.isEmpty() || images != null){
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


    }

    //관광지 후기 가져오기
    public PlaceReviewRes getReviews(Long placeId, Pageable page){
        Place place = getPlace(placeId);
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("createdAt").descending());
        Page<PlaceReview> placeReviewPage = placeReviewRepository.findAllByPlaceOrderByCreatedAtDesc(place, pageable);
        List<PlaceReivewListDto> placeReviewListDto = placeReviewPage.getContent().stream()
                .map(this::getPlaceReviewDto)
                .toList();

        return PlaceReviewRes.of(place, placeReviewListDto, placeReviewPage.getNumber(), placeReviewPage.getSize(), placeReviewPage.getTotalPages());

    }

    //나의 여행지 후기
    public MyPlaceReviewRes getMyReviews(Member member, Pageable page){
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("createdAt").descending());
        Page<PlaceReview> placeReviewPage = placeReviewRepository.findAllByMemberOrderByCreatedAtDesc(member, pageable);
        return new MyPlaceReviewRes(placeReviewPage.getContent().stream()
                .map(this::getMyPlaceReview)
                .toList(),placeReviewPage.getNumber(),placeReviewPage.getSize(),placeReviewPage.getTotalPages());
    }

    //나의 여행지 후기 삭제
    @Transactional
    public void deleteMyPlaceReview(Member member, Long reviewId){

        PlaceReview placeReview = placeReviewRepository.findById(reviewId).orElseThrow(
                () -> new ApplicationException(ErrorCode.NOT_FOUND_PLACE_REVIEW_EXCEPTION));

        if(placeReview.getMember() != member){
            new ApplicationException(ErrorCode.FORBIDDEN_EXCEPTION);
        }
        placeReviewImgRepository.findAllByPlaceReview(placeReview).forEach(
                placeReviewImg -> s3Client.delete(StringUtils.substringAfter(placeReviewImg.getImgUrl(), partToFind))
        );

        placeReviewImgRepository.deleteByPlaceReview(placeReview);
        placeReviewRepository.delete(placeReview);

    }

    //나의 여행지 후기 보기(1개)
    public MyReview getReview(Member member, Long reviewId){
        PlaceReview placeReview = placeReviewRepository.findById(reviewId).orElseThrow(
                () -> new ApplicationException(ErrorCode.NOT_FOUND_PLACE_REVIEW_EXCEPTION));

        if(placeReview.getMember() != member){
            new ApplicationException(ErrorCode.FORBIDDEN_EXCEPTION);
        }

        List<String> list = placeReviewImgRepository.findAllByPlaceReview(placeReview)
                .stream()
                .map(PlaceReviewImg::getImgUrl)
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            list.add(placeReview.getPlace().getFirstImg());
        }

        return MyReview.of(placeReview, list);
    }

    //북마크한 여행지 이름만 가져오기
    public List<PlaceBookmarkDto> getBookmarkPlaceNames(Member member){
        List<PlaceBookmark> placeBookmarkList = placeBookmarkRepository.findAllByMemberOrderByPlaceNameAsc(member);
        if(placeBookmarkList.isEmpty() || placeBookmarkList==null)
            return new ArrayList<>();

        return placeBookmarkList.stream().map(PlaceBookmarkDto::of).toList();
    }


    @Transactional
    // 북마크 상태변경
    public void bookmark(Member member, Long placeId){
        Place place = getPlace(placeId);

        PlaceBookmark placeBookmark = placeBookmarkRepository.findPlaceBookmarkByPlaceAndMember(place, member);// 북마크 설정
        if (placeBookmark == null) {
            PlaceBookmark newPlaceBookmark = PlaceBookmark.builder()
                    .place(place)
                    .member(member)
                    .build();
            placeBookmarkRepository.save(newPlaceBookmark);
        } else {
            // 북마크 해체
            placeBookmarkRepository.delete(placeBookmark);
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

    private Place getPlace(Long placeId){
        return placeRepository.findById(placeId).orElseThrow(
                ()->new ApplicationException(ErrorCode.NOT_FOUND_PLACE_EXCEPTION));
    }

    private PlaceReivewListDto getPlaceReviewDto(PlaceReview placeReview){
       List<PlaceReviewImg> imgList = placeReviewImgRepository.findAllByPlaceReview(placeReview);
       if(imgList.size()>0){
           List<String> imgUrls = imgList.stream()
                   .map(PlaceReviewImg::getImgUrl)
                   .collect(Collectors.toList());
           return PlaceReivewListDto.of(placeReview, imgUrls);
       }
       return PlaceReivewListDto.of(placeReview, new ArrayList<>());

    }

    private MyPlaceReviewDto getMyPlaceReview(PlaceReview placeReview ){
        List<PlaceReviewImg> imgList = placeReviewImgRepository.findAllByPlaceReview(placeReview);
        return MyPlaceReviewDto.of(placeReview, imgList.get(0).getImgUrl());

    }



}
