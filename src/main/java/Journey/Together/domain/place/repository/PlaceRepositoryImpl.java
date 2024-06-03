package Journey.Together.domain.place.repository;

import Journey.Together.domain.member.repository.MemberRepository;
import Journey.Together.domain.place.dto.response.PlaceRes;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.entity.QDisabilityPlaceCategory;
import Journey.Together.domain.place.entity.QDisabilitySubCategory;
import Journey.Together.domain.placeBookbark.entity.PlaceBookmark;
import Journey.Together.domain.placeBookbark.entity.QPlaceBookmark;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static Journey.Together.domain.place.entity.QDisabilityPlaceCategory.disabilityPlaceCategory;
import static Journey.Together.domain.place.entity.QDisabilitySubCategory.disabilitySubCategory;
import static Journey.Together.domain.placeBookbark.entity.QPlaceBookmark.placeBookmark;
import static org.springframework.util.StringUtils.isEmpty;
import static Journey.Together.domain.place.entity.QPlace.place;

public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PlaceRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    // Long placeId,
    //        String name,
    //        String image,
    //        List<String> disability,
    //        String address

    @Override
    public List<PlaceRes> search(String category, String query, String disabilityType, String detailFilter, String areacode, String sigungucode, String arrange, Integer pageNo) {
        return new ArrayList<>();
    }

}
