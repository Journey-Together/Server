package Journey.Together.domain.place.repository;


import Journey.Together.domain.place.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryCustom {

    @Query(value = "SELECT * FROM place p WHERE p.first_img IS NOT NULL AND p.first_img <> '' ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Place> findRandomProducts(@Param("count") int count);

    @Query(value = "SELECT * FROM place p WHERE p.first_img IS NOT NULL AND p.first_img <> '' AND area_code = :areacode AND sigungu_code = :sigungucode " +
            "ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Place> findAroundProducts(@Param("areacode") String areacode, @Param("sigungucode") String sigungucode,
                                   @Param("count") int count);

    Place findPlaceById(Long id);

    Place findPlaceByName(String name);

    /*
    검색어 기반 목록 조회 최신순
   */
    Page<Place> findAllByNameContainsOrderByCreatedAtDesc(@Param("searchWord") String searchWord,Pageable pageable);

    Page<Place> findByIsActiveTrue(Pageable pageable);
    Page<Place> findAll(Pageable pageable);

    long count();
}
