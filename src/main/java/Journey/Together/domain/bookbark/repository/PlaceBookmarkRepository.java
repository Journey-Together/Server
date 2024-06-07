package Journey.Together.domain.bookbark.repository;


import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.bookbark.entity.PlaceBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceBookmarkRepository extends JpaRepository<PlaceBookmark, Long> {

    List<PlaceBookmark> findAllByPlace(Place place);
    List<PlaceBookmark> findAllByMemberOrderByCreatedAtDesc(Member member);

    List<PlaceBookmark> findAllByMemberOrderByPlaceNameAsc(Member member);

    PlaceBookmark findPlaceBookmarkByPlaceAndMember(Place place, Member member);


}
