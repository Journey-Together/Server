package Journey.Together.domain.place.repository;

import Journey.Together.domain.member.repository.MemberRepository;
import Journey.Together.domain.place.dto.response.PlaceRes;
import Journey.Together.domain.place.dto.response.SearchPlace;
import Journey.Together.domain.place.dto.response.SearchPlaceRes;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.entity.QDisabilityCategory;
import Journey.Together.domain.place.entity.QDisabilityPlaceCategory;
import Journey.Together.domain.place.entity.QDisabilitySubCategory;
import Journey.Together.domain.placeBookbark.entity.PlaceBookmark;
import Journey.Together.domain.placeBookbark.entity.QPlaceBookmark;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static Journey.Together.domain.place.entity.QDisabilityCategory.disabilityCategory;
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
    public SearchPlace search(String category, String query, List<Long> disabilityType, List<Long> detailFilter, String areacode, String sigungucode, String arrange, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 50);
        Long total = 0L;

        List<Place> places = queryFactory
                .select(place)
                .from(place)
                .join(place.placeDisabilityCategories, disabilityPlaceCategory)
                .where(place.name.contains(query))
                .where(categoryEq(category), disabilityTypeHas(disabilityType), detailFilterHas(detailFilter),
                        areacodeEq(areacode), sigungucodeEq(sigungucode))
                .orderBy(arg(arrange))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        total = queryFactory
                .select(place.count())
                .from(place)
                .join(place.placeDisabilityCategories, disabilityPlaceCategory)
                .where(place.name.contains(query))
                .where(categoryEq(category), disabilityTypeHas(disabilityType), detailFilterHas(detailFilter),
                        areacodeEq(areacode), sigungucodeEq(sigungucode))
                .fetchOne();

        return new SearchPlace(places,total);
    }
    private BooleanExpression categoryEq(String category) {
        List<String> categoryList = Arrays.asList("A01", "A02", "A03", "A04", "B01", "C01");

        if(category.equals("ROOM"))
            categoryList.add("B02");
        else if (category.equals("RESTAURANT"))
            categoryList.add("A05");

        return place.category.in(categoryList);
    }

    private BooleanExpression disabilityTypeHas(List<Long> disabilityType) {
        if(disabilityType==null||disabilityType.isEmpty())
            return null;
        return place.id.in(
                JPAExpressions
                        .select(disabilityPlaceCategory.place.id)
                        .from(disabilityPlaceCategory)
                        .where(disabilityPlaceCategory.subCategory.category.id.in(disabilityType))
                        .groupBy(disabilityPlaceCategory.place.id)
                        .having(disabilityPlaceCategory.place.id.count().goe((long)disabilityType.size()))
        );
    }

    private BooleanExpression detailFilterHas(List<Long> detailFilter) {
        if(detailFilter==null||detailFilter.isEmpty())
            return null;
        return place.id.in(
                JPAExpressions
                        .select(disabilityPlaceCategory.place.id)
                        .from(disabilityPlaceCategory)
                        .where(disabilityPlaceCategory.subCategory.id.in(detailFilter))
                        .groupBy(disabilityPlaceCategory.place.id)
                        .having(disabilityPlaceCategory.place.id.count().goe((long)detailFilter.size()))
        );
    }

    private BooleanExpression areacodeEq(String areacode) {
        return areacode != null ? place.areaCode.eq(areacode) : null;
    }

    private BooleanExpression sigungucodeEq(String sigungucode) {
        return sigungucode != null ? place.sigunguCode.eq(sigungucode) : null;
    }

    private OrderSpecifier<?> arg(String arrange) {
        if ("A".equals(arrange)) { // 최신순
            return place.createdAt.desc();
        } else if ("B".equals(arrange)) { // 인기순
//            return placeBookmark.place.count().desc();
            return place.name.asc();
        } else if ("C".equals(arrange)) { // 가나다순
            return place.name.asc();
        } else {
            return place.name.asc();
        }
    }
}
