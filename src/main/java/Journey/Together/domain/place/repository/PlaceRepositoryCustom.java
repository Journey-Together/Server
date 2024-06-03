package Journey.Together.domain.place.repository;

import Journey.Together.domain.place.dto.response.PlaceRes;

import java.util.List;

public interface PlaceRepositoryCustom {
    List<PlaceRes> search(String category, String query,String disabilityType,String detailFilter, String areacode, String sigungucode, String arrange, Integer pageNo);
}
