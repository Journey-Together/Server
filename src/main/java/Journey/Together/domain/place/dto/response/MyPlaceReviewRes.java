package Journey.Together.domain.place.dto.response;

import java.util.List;

public record MyPlaceReviewRes(

        Long reviewNum,
        List<MyPlaceReviewDto> myPlaceReviewDtoList,
        Integer pageNo,
        Integer pageSize,
        Integer totalPages
){
}
