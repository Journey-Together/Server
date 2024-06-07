package Journey.Together.domain.placeBookbark.repository;


import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.placeBookbark.entity.PlaceBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface PlaceBookmarkRepository extends JpaRepository<PlaceBookmark, Long> {

    List<PlaceBookmark> findAllByPlace(Place place);

    List<PlaceBookmark> findAllByMemberOrderByPlaceNameAsc(Member member);

    PlaceBookmark findPlaceBookmarkByPlaceAndMember(Place place, Member member);


}
