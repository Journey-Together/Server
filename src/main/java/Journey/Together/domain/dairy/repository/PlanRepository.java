package Journey.Together.domain.dairy.repository;

import Journey.Together.domain.dairy.entity.Plan;
import Journey.Together.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Plan findPlanByMemberAndPlanIdAndDeletedAtIsNull(Member member,Long planId);
    List<Plan> findAllByMemberAndDeletedAtIsNull(Member member);
    void deletePlanByPlanId(Long id);
}
