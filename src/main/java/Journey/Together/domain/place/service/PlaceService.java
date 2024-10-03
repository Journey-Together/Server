package Journey.Together.domain.place.service;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.dto.request.PlaceReviewReq;
import Journey.Together.domain.place.dto.request.UpdateReviewDto;
import Journey.Together.domain.place.dto.response.*;
import Journey.Together.domain.place.dto.response.*;
import Journey.Together.domain.place.entity.DisabilityPlaceCategory;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.DisabilityPlaceCategoryRepository;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.domain.bookbark.entity.PlaceBookmark;
import Journey.Together.domain.bookbark.repository.PlaceBookmarkRepository;
import Journey.Together.domain.place.entity.PlaceReview;
import Journey.Together.domain.place.entity.PlaceReviewImg;
import Journey.Together.domain.place.repository.PlaceReviewImgRepository;
import Journey.Together.domain.place.repository.PlaceReviewRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.exception.ErrorResponse;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.Pageable;
import Journey.Together.global.exception.Success;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
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

    private final RestHighLevelClient client;
    private final S3Client s3Client;

    private static final String POST_IMAGE_FOLDER_NAME = "reviews/";
    private final Integer recommnedPlaceNum = 3;
    private final Integer aroundPlaceNum = 6;
    private final Integer autocompleteNum = 10;
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

        Boolean isReview = false;
        Boolean isMark = false;

        Place place = getPlace(placeId);
        Long myPlaceReviewId;

        List<PlaceBookmark> placeBookmarkList = placeBookmarkRepository.findAllByPlaceAndMember(place,member);
        if(placeBookmarkList.size()>0)
            isMark =true;

        List<Long> disability = disabilityPlaceCategoryRepository.findDisabilityCategoryIds(placeId);
        List<SubDisability> subDisability = disabilityPlaceCategoryRepository.findDisabilitySubCategory(placeId).stream().map(SubDisability::of).toList();
        Pageable pageable = PageRequest.of(0, 2);
        List<PlaceReview> placeReviews = placeReviewRepository.findTop2ByPlaceAndReportIsNullOrReportFalseOrderByCreatedAtDesc(place,pageable);

        if(placeReviewRepository.findPlaceReviewByMemberAndPlace(member,place) != null) {
            isReview = true;
            PlaceReview myPlaceReview = placeReviewRepository.findPlaceReviewByMemberAndPlace(member,place);
            myPlaceReviewId = myPlaceReview.getId();
        } else {
            myPlaceReviewId = null;
        }

        if(placeReviews.size()<0)
            return PlaceDetailRes.of(place, isMark, placeBookmarkList.size(), disability, subDisability, null, isReview);

        List<PlaceReviewDto> reviewList = new ArrayList<>();

        placeReviews.forEach(placeReview -> {
            List<PlaceReviewImg> placeReviewImgs = placeReviewImgRepository.findAllByPlaceReview(placeReview);
            if (placeReviewImgs.size() > 0) {
                if(Objects.equals(placeReview.getId(), myPlaceReviewId) && myPlaceReviewId != null)
                    reviewList.add(PlaceReviewDto.of(placeReview, s3Client.getUrl()+placeReview.getMember().getProfileUuid()+"/profile_"+placeReview.getMember().getProfileUuid(),placeReviewImgs.stream().map(PlaceReviewImg::getImgUrl).toList(),true));
                else
                    reviewList.add(PlaceReviewDto.of(placeReview, s3Client.getUrl()+placeReview.getMember().getProfileUuid()+"/profile_"+placeReview.getMember().getProfileUuid(),placeReviewImgs.stream().map(PlaceReviewImg::getImgUrl).toList(),false));
            } else{
                if(Objects.equals(placeReview.getId(), myPlaceReviewId) && myPlaceReviewId != null)
                    reviewList.add(PlaceReviewDto.of(placeReview, s3Client.getUrl()+placeReview.getMember().getProfileUuid()+"/profile_"+placeReview.getMember().getProfileUuid(),null,true));
                else
                    reviewList.add(PlaceReviewDto.of(placeReview, s3Client.getUrl()+placeReview.getMember().getProfileUuid()+"/profile_"+placeReview.getMember().getProfileUuid(),null,false));
            }
        });

        return PlaceDetailRes.of(place, isMark, placeBookmarkList.size(), disability, subDisability, reviewList, isReview);

    }

    public PlaceDetailGuestRes getGeustPlaceDetail(Long placeId){
        // PlaceDetailRes of(Place place, Boolean isMark, Integer bookmarkNum, List<String> disability, List<String> subDisability, List< PlaceReviewDto > reviewList)

        Place place = getPlace(placeId);
        Boolean myReview = false;

        List<PlaceBookmark> placeBookmarkList = placeBookmarkRepository.findAllByPlace(place);
        List<Long> disability = disabilityPlaceCategoryRepository.findDisabilityCategoryIds(placeId);
        List<SubDisability> subDisability = disabilityPlaceCategoryRepository.findDisabilitySubCategory(placeId).stream().map(SubDisability::of).toList();

        Pageable pageable = PageRequest.of(0, 2);
        List<PlaceReview> placeReviews = placeReviewRepository.findTop2ByPlaceAndReportIsNullOrReportFalseOrderByCreatedAtDesc(place,pageable);

        if(placeReviews.size()<0)
            return PlaceDetailGuestRes.of(place, placeBookmarkList.size(), disability, subDisability, null);

        List<PlaceReviewDto> reviewList = new ArrayList<>();

        placeReviews.forEach(placeReview -> {
            List<PlaceReviewImg> placeReviewImgs = placeReviewImgRepository.findAllByPlaceReview(placeReview);
            if (placeReviewImgs.size() > 0) {
                reviewList.add(PlaceReviewDto.of(placeReview, s3Client.getUrl()+placeReview.getMember().getProfileUuid()+"/profile_"+placeReview.getMember().getProfileUuid(),placeReviewImgs.stream().map(PlaceReviewImg::getImgUrl).toList(), myReview));
            } else
                reviewList.add(PlaceReviewDto.of(placeReview,s3Client.getUrl()+placeReview.getMember().getProfileUuid()+"/profile_"+placeReview.getMember().getProfileUuid(), null, myReview));
        });


        return PlaceDetailGuestRes.of(place, placeBookmarkList.size(), disability, subDisability, reviewList);

    }

    public SearchPlaceRes searchPlaceList(String category, String query, List<Long> disabilityType, List<Long> detailFilter, String areacode, String sigungucode, String arrange,
                                          Pageable pageable){
        List<PlaceRes> placeResList =new ArrayList<>();

        SearchPlace searchPlace = placeRepository.searchList(category, query, disabilityType, detailFilter, areacode, sigungucode, arrange, pageable);
        searchPlace.places().forEach(
                place -> placeResList.add(PlaceRes.of(place,disabilityPlaceCategoryRepository.findDisabilityCategoryIds(place.getId())))
        );

        return new SearchPlaceRes(placeResList, pageable.getPageNumber(), pageable.getPageSize(), searchPlace.size());
    }

    public List<PlaceRes> searchPlaceMap(String category, List<Long> disabilityType, List<Long> detailFilter,String arrange,
                                         Double minX, Double maxX, Double minY, Double maxY){
        List<PlaceRes> placeResList =new ArrayList<>();

        List<Place> places = placeRepository.searchMap(category, disabilityType, detailFilter, arrange, minX, maxX,minY, maxY);
        places.forEach(
                place -> placeResList.add(PlaceRes.of(place,disabilityPlaceCategoryRepository.findDisabilityCategoryIds(place.getId())))
        );

        return placeResList;
    }


    //여행지 후기 생성
    @Transactional
    public void createReview(Member member, List<MultipartFile> images, PlaceReviewReq placeReviewReq, Long placeId){
        Place place = getPlace(placeId);

        if(placeReviewRepository.findPlaceReviewByMemberAndPlace(member,place) != null)
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
                throw new ApplicationException(ErrorCode.NOT_ADD_IMAGE_EXCEPTION);
            }
        }


    }

    //관광지 후기 가져오기
    public PlaceReviewRes getReviews(Member member,Long placeId, Pageable page){
        Place place = getPlace(placeId);
        List<PlaceReivewListDto> placeReviewList =new ArrayList<>();
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("createdAt").descending());
        Page<PlaceReview> placeReviewPage = placeReviewRepository.findAllByPlaceAndReportIsNullOrReportFalseOrderByCreatedAtDesc(place, pageable);
        Long reviewNum = placeReviewRepository.countPlaceReviewByPlaceAndReportIsNullOrReportFalseOrderByCreatedAtDesc(place);
        placeReviewPage.getContent().forEach(
                placeReview -> {
                    if(Objects.equals(placeReview.getMember().getMemberId(), member.getMemberId()))
                        placeReviewList.add(PlaceReivewListDto.of(placeReview,getImgUrls(placeReview),s3Client.getUrl(),true));
                    else
                        placeReviewList.add(PlaceReivewListDto.of(placeReview,getImgUrls(placeReview),s3Client.getUrl(),false));
                }
        );

        return PlaceReviewRes.of(place, placeReviewList, reviewNum, placeReviewPage.getNumber(), placeReviewPage.getSize(), placeReviewPage.getTotalPages());

    }

    public PlaceReviewRes getReviewsGeust(Long placeId, Pageable page){
        Place place = getPlace(placeId);
        List<PlaceReivewListDto> placeReviewList =new ArrayList<>();
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("createdAt").descending());
        Long reviewNum = placeReviewRepository.countPlaceReviewByPlaceAndReportIsNullOrReportFalseOrderByCreatedAtDesc(place);
        Page<PlaceReview> placeReviewPage = placeReviewRepository.findAllByPlaceAndReportIsNullOrReportFalseOrderByCreatedAtDesc(place, pageable);
        placeReviewPage.getContent().forEach(
                placeReview -> {
                    placeReviewList.add(PlaceReivewListDto.of(placeReview,getImgUrls(placeReview),s3Client.getUrl(),false));
                }
        );

        return PlaceReviewRes.of(place, placeReviewList, reviewNum, placeReviewPage.getNumber(), placeReviewPage.getSize(), placeReviewPage.getTotalPages());

    }
    //나의 여행지 후기
    public MyPlaceReviewRes getMyReviews(Member member, Pageable page){
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("createdAt").descending());
        Page<PlaceReview> placeReviewPage = placeReviewRepository.findAllByMemberOrderByCreatedAtDesc(member, pageable);

        Long reviewNum = placeReviewRepository.countPlaceReviewByMember(member);

        return new MyPlaceReviewRes(reviewNum,placeReviewPage.getContent().stream()
                .map(this::getMyPlaceReview)
                .toList(), placeReviewPage.getNumber(),placeReviewPage.getSize(),placeReviewPage.getTotalPages());
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

    private Place getPlace(Long placeId){
        return placeRepository.findById(placeId).orElseThrow(
                ()->new ApplicationException(ErrorCode.NOT_FOUND_PLACE_EXCEPTION));
    }

    private List<String> getImgUrls(PlaceReview placeReview){
       List<PlaceReviewImg> imgList = placeReviewImgRepository.findAllByPlaceReview(placeReview);
       if(imgList.size()>0){
           List<String> imgUrls = imgList.stream()
                   .map(PlaceReviewImg::getImgUrl)
                   .collect(Collectors.toList());
           return imgUrls;
       }
       return new ArrayList<>();

    }

    private MyPlaceReviewDto getMyPlaceReview(PlaceReview placeReview ){
        List<String> list = placeReviewImgRepository.findAllByPlaceReview(placeReview)
                .stream()
                .map(PlaceReviewImg::getImgUrl)
                .toList();

        return MyPlaceReviewDto.of(placeReview, list);

    }

    @Transactional
    public void updateMyPlaceReview(Member member, UpdateReviewDto updateReviewDto, List<MultipartFile> addImages, Long reviewId) {
        PlaceReview placeReview = placeReviewRepository.findById(reviewId).orElseThrow(
                () -> new ApplicationException(ErrorCode.NOT_FOUND_PLACE_REVIEW_EXCEPTION));

        if(placeReview.getMember().getMemberId() != member.getMemberId()){
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        if (addImages != null) {
            try {
                for(MultipartFile file : addImages) {
                    String uuid = UUID.randomUUID().toString();
                    final String imageUrl = s3Client.upload(file, POST_IMAGE_FOLDER_NAME+member.getMemberId(), uuid);
                    PlaceReviewImg placeReviewImg = PlaceReviewImg.builder().placeReview(placeReview).imgUrl(imageUrl).build();
                    placeReviewImgRepository.save(placeReviewImg);
                }
            } catch (RuntimeException e) {
                throw new ApplicationException(ErrorCode.NOT_ADD_IMAGE_EXCEPTION);
            }
        }

        if (updateReviewDto != null) {
            if (updateReviewDto.content() != null) {
                placeReview.setContent(updateReviewDto.content());
            }if (updateReviewDto.date() != null) {
                placeReview.setDate(updateReviewDto.date());
            }if (updateReviewDto.grade() != null) {
                placeReview.setGrade(updateReviewDto.grade());
            }
            if (updateReviewDto.deleteImgUrls() != null) {
                updateReviewDto.deleteImgUrls().forEach(
                        deleteImg -> {
                            placeReviewImgRepository.deleteByImgUrl(deleteImg);
                            s3Client.delete(StringUtils.substringAfter(deleteImg, partToFind));
                        }
                );
            }
        }

        placeReviewRepository.save(placeReview);

    }

    public List<Map<String, Object>> searchPlaceComplete(String query) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest("places");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.matchQuery("name", query));
        searchSourceBuilder.size(autocompleteNum); // 상위 10개의 결과만 반환

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        List<String> results = Arrays.stream(searchResponse.getHits().getHits())
                .map(hit -> hit.getSourceAsMap().get("name").toString())
                .toList();

        results.forEach(keyword -> {
            Place place = placeRepository.findPlaceByName(keyword);
            Long placeId = null;
            if (place != null) {
                placeId = place.getId();
            }

            Map<String, Object> map = new HashMap<>();
            map.put("keyword", keyword);
            map.put("placeId", placeId);

            list.add(map);
        });

        return list;
    }



}
