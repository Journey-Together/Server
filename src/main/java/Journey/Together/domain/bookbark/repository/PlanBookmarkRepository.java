package Journey.Together.domain.bookbark.repository;


import Journey.Together.domain.bookbark.entity.PlanBookmark;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanBookmarkRepository extends JpaRepository<PlanBookmark, Long> {

    List<PlanBookmark> findAllByMemberOrderByCreatedAtDesc(Member member);

    PlanBookmark findPlanBookmarkByPlanAndMember(Plan plan, Member member);


}
