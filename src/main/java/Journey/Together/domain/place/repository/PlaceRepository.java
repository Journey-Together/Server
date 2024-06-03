package Journey.Together.domain.place.repository;


import Journey.Together.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query(value = "SELECT * FROM place p ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Place> findRandomProducts(@Param("count") int count);

    @Query(value = "SELECT * FROM place p WHERE area_code = :areacode AND sigungu_code = :sigungucode " +
            "ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Place> findAroundProducts(@Param("areacode") String areacode, @Param("sigungucode") String sigungucode,
                                   @Param("count") int count);

    Place findPlaceById(Long id);
}
