package Journey.Together.domain.dairy.repository;

import Journey.Together.domain.dairy.entity.Plan;
import Journey.Together.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Plan findPlanByMemberAndPlanIdAndDeletedAtIsNull(Member member,Long planId);
    List<Plan> findAllByMemberAndDeletedAtIsNull(Member member);
    Plan findPlanByMemberAndPlanIdAndEndDateIsBeforeAndDeletedAtIsNull(Member member, Long planId, LocalDate today);
    void deletePlanByPlanId(Long id);

}
