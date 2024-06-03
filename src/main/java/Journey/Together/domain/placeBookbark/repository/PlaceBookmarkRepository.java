package Journey.Together.domain.placeBookbark.repository;


import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.placeBookbark.entity.PlaceBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceBookmarkRepository extends JpaRepository<PlaceBookmark, Long> {

    List<PlaceBookmark> findAllByPlace(Place place);


}
