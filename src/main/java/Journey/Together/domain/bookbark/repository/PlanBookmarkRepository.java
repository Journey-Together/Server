package Journey.Together.domain.bookbark.repository;


import Journey.Together.domain.bookbark.entity.PlaceBookmark;
import Journey.Together.domain.bookbark.entity.PlanBookmark;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanBookmarkRepository extends JpaRepository<PlanBookmark, Long> {

    List<PlanBookmark> findAllByMemberOrderByCreatedAtDesc(Member member);


}
