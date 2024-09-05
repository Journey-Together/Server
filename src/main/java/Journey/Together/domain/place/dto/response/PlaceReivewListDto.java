package Journey.Together.domain.place.dto.response;

import Journey.Together.domain.place.entity.PlaceReview;

import java.time.LocalDate;
import java.util.List;

public record PlaceReivewListDto(
        Long reviewId,
        String nickname,
        String profileImg,
        String content,
        List<String> imageList,
        Float grade,
        LocalDate date,
        Boolean myReview
) {
    public static PlaceReivewListDto of(PlaceReview placeReview, List<String> imageList, String s3Url, Boolean myReview){
        return new PlaceReivewListDto(placeReview.getId(),placeReview.getMember().getNickname(), s3Url+placeReview.getMember().getProfileUuid()+"/profile",
                placeReview.getContent(), imageList, placeReview.getGrade(), placeReview.getDate(), myReview);
    }
}
