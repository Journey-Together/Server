package Journey.Together.domain.place.repository;

import Journey.Together.domain.bookbark.entity.QPlaceBookmark;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.repository.MemberRepository;
import Journey.Together.domain.place.dto.request.UpdateReviewDto;
import Journey.Together.domain.place.dto.response.PlaceRes;
import Journey.Together.domain.place.dto.response.SearchPlace;
import Journey.Together.domain.place.dto.response.SearchPlaceRes;
import Journey.Together.domain.place.entity.Place;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jpa.JPAExpressions;
import Journey.Together.domain.place.entity.QDisabilityCategory;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.springframework.data.domain.Pageable;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static Journey.Together.domain.bookbark.entity.QPlaceBookmark.placeBookmark;
import static Journey.Together.domain.place.entity.QDisabilityCategory.disabilityCategory;
import static Journey.Together.domain.place.entity.QDisabilityPlaceCategory.disabilityPlaceCategory;
import static Journey.Together.domain.place.entity.QDisabilitySubCategory.disabilitySubCategory;
import static Journey.Together.domain.place.entity.QPlace.place;


public class PlaceRepositoryImpl implements PlaceRepositoryCustom {
    private static List<Long> default_disability1 = Arrays.asList(2L, 3L, 4L, 5L, 10L, 11L, 12L);
    private static List<Long> default_disability2 = Arrays.asList(17L, 18L, 19L, 5L, 20L);
    private static List<Long> default_disability3 = Arrays.asList(22L, 24L);
    private static List<Long> default_disability4 = Arrays.asList(28L);

    private final JPAQueryFactory queryFactory;

    public PlaceRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public SearchPlace searchList(String category, String query, List<Long> disabilityType, List<Long> detailFilter, String areacode, String sigungucode, String arrange,
                              Pageable pageable) {

        // 상세필터적용
        List<Place> places = queryFactory
                .selectDistinct(place)
                .from(place)
                .join(place.placeDisabilityCategories, disabilityPlaceCategory)
                .where(categoryEq(category), queryContains(query), disabilityTypeHas(disabilityType),
                        detailFilterHas(detailFilter).or(defaultDisabilityTypes(disabilityType)),
                        areacodeEq(areacode), sigungucodeEq(sigungucode))
                .orderBy(arg(arrange))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        long total = queryFactory
                .select(place.countDistinct())
                .from(place)
                .join(place.placeDisabilityCategories, disabilityPlaceCategory)
                .where(categoryEq(category), queryContains(query), disabilityTypeHas(disabilityType),
                        detailFilterHas(detailFilter).or(defaultDisabilityTypes(disabilityType)),
                        areacodeEq(areacode), sigungucodeEq(sigungucode))
                .fetchOne();


        return new SearchPlace(places,total);
    }

    @Override
    public List<Place> searchMap(String category,List<Long> disabilityType, List<Long> detailFilter, String arrange,
                                  Double minX, Double maxX, Double minY, Double maxY) {
        Long total = 0L;

        List<Place> places = queryFactory
                .selectDistinct(place)
                .from(place)
                .join(place.placeDisabilityCategories, disabilityPlaceCategory)
                .where(categoryEq(category), disabilityTypeHas(disabilityType), detailFilterHas(detailFilter),
                        mapIn(minX,maxX,minY,maxY) )
                .orderBy(arg(arrange))
                .fetch();

        return places;
    }

    private BooleanExpression categoryEq(String category) {
        List<String> categoryList = new ArrayList<>(Arrays.asList("A01", "A02", "A03", "A04", "B01", "C01"));

        if(category.equals("ROOM"))
            return place.category.eq("B02");
        else if (category.equals("RESTAURANT"))
            return place.category.eq("A05");

        return place.category.in(categoryList);
    }

    private BooleanExpression mapIn(Double minX, Double maxX, Double minY, Double maxY){
        if(minX == null && maxX == null && minY == null && maxY == null)
            return null;
        if(minX == null || maxX == null || minY == null || maxY == null)
            throw new ApplicationException(ErrorCode.INVALID_MAP_EXCEPTION);
        return place.id.in(
                JPAExpressions.select(
                                place.id)
                        .from(place)
                        .where(
                                place.mapX.between(minX, maxX),
                                place.mapY.between(minY, maxY)
                        )
        );

    }

    private BooleanExpression disabilityTypeHas(List<Long> disabilityType) {
        if(disabilityType == null || disabilityType.isEmpty())
            return null;

        return checkDisabilityTypes(disabilityType, 0);
    }


    private BooleanExpression checkDisabilityTypes(List<Long> disabilityType, int index) {
        if (index == disabilityType.size() - 1) {
            return disabilityPlaceCategory.place.in(
                    JPAExpressions.select(disabilityPlaceCategory.place)
                            .from(disabilityPlaceCategory)
                            .innerJoin(disabilityPlaceCategory.subCategory, disabilitySubCategory)
                            .join(disabilitySubCategory.category, disabilityCategory)
                            .groupBy(disabilityPlaceCategory.place)
                            .where(disabilityPlaceCategory.subCategory.category.id.in(disabilityType.get(index)))
            );
        } else {
            return disabilityPlaceCategory.place.in(
                    JPAExpressions.select(disabilityPlaceCategory.place)
                            .from(disabilityPlaceCategory)
                            .innerJoin(disabilityPlaceCategory.subCategory, disabilitySubCategory)
                            .join(disabilitySubCategory.category, disabilityCategory)
                            .groupBy(disabilityPlaceCategory.place)
                            .where(disabilityPlaceCategory.subCategory.category.id.in(disabilityType.get(index)))
            ).and(checkDisabilityTypes(disabilityType, index + 1));
        }
    }


    private BooleanExpression detailFilterHas(List<Long> detailFilter) {
        if(detailFilter == null || detailFilter.isEmpty())
            return null;

        return checkSubDisabilityTypes(detailFilter, 0);
    }


    private BooleanExpression checkSubDisabilityTypes(List<Long> detailFilter, int index) {
        if (index == detailFilter.size() - 1) {
            return disabilityPlaceCategory.place.in(
                    JPAExpressions.select(disabilityPlaceCategory.place)
                            .from(disabilityPlaceCategory)
                            .innerJoin(disabilityPlaceCategory.subCategory, disabilitySubCategory)
                            .groupBy(disabilityPlaceCategory.place)
                            .where(disabilityPlaceCategory.subCategory.id.in(detailFilter.get(index)))
            );
        } else {
            return disabilityPlaceCategory.place.in(
                    JPAExpressions.select(disabilityPlaceCategory.place)
                            .from(disabilityPlaceCategory)
                            .innerJoin(disabilityPlaceCategory.subCategory, disabilitySubCategory)
                            .groupBy(disabilityPlaceCategory.place)
                            .where(disabilityPlaceCategory.subCategory.id.in(detailFilter.get(index)))
            ).and(checkSubDisabilityTypes(detailFilter, index + 1));
        }
    }

    private BooleanExpression defaultDisabilityTypes(List<Long> disabilityType) {
        if(disabilityType == null || disabilityType.isEmpty())
            return null;

        List<Long> default_id = new ArrayList<>();

        if(disabilityType.contains(1L))
            default_id.addAll(default_disability1);

        if(disabilityType.contains(2L))
            default_id.addAll(default_disability2);

        if(disabilityType.contains(3L))
            default_id.addAll(default_disability3);

        if(disabilityType.contains(4L))
            default_id.addAll(default_disability4);


        return disabilityPlaceCategory.place.in(
                JPAExpressions.select(disabilityPlaceCategory.place)
                        .from(disabilityPlaceCategory)
                        .innerJoin(disabilityPlaceCategory.subCategory, disabilitySubCategory)
                        .groupBy(disabilityPlaceCategory.place)
                        .where(disabilityPlaceCategory.subCategory.id.in(default_id)
                        ));
    }


    private BooleanExpression areacodeEq(String areacode) {
        return areacode != null ? place.areaCode.eq(areacode) : null;
    }

    private BooleanExpression sigungucodeEq(String sigungucode) {
        return sigungucode != null ? place.sigunguCode.eq(sigungucode) : null;
    }

    private BooleanExpression queryContains(String query) {
        return query != null ? place.name.contains(query) : null;
    }

    private OrderSpecifier<?> arg(String arrange) {
        if ("A".equals(arrange)) { // 최신순
            return place.createdAt.desc();
        } else if ("B".equals(arrange)) { // 인기순
            NumberPath<Long> placeIdCount = Expressions.numberPath(Long.class, "placeIdCount");
            SubQueryExpression<Long> subQuery = JPAExpressions.select(placeBookmark.place.id.count())
                    .from(placeBookmark)
                    .where(placeBookmark.place.eq(place));
            return new OrderSpecifier<>(Order.DESC, subQuery);
        } else if ("C".equals(arrange)) { // 가나다순
            return place.name.asc();
        } else {
            return place.createdAt.desc(); // default = 최신순
        }
    }

}
