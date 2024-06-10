package Journey.Together.domain.place.repository;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.dto.request.UpdateReviewDto;
import Journey.Together.domain.place.dto.response.PlaceRes;
import Journey.Together.domain.place.dto.response.SearchPlace;
import Journey.Together.domain.place.dto.response.SearchPlaceRes;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PlaceRepositoryCustom {
    SearchPlace search(String category, String query, List<Long> disabilityType, List<Long> detailFilter, String areacode, String sigungucode, String arrange, Pageable pageable,
                       Double minX, Double maxX, Double minY, Double maxY);

}
