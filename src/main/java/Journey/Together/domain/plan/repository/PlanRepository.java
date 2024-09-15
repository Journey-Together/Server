package Journey.Together.domain.plan.repository;

import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Plan findPlanByPlanIdAndDeletedAtIsNull(Long planId);
    Plan findPlanByMemberAndPlanIdAndDeletedAtIsNull(Member member,Long planId);
    List<Plan> findAllByMemberAndDeletedAtIsNull(Member member);
    Plan findPlanByMemberAndPlanIdAndEndDateIsBeforeAndDeletedAtIsNull(Member member, Long planId, LocalDate today);
    void deletePlanByPlanId(Long id);
    // 오늘을 포함하지 않고 지난 계획들
    Page<Plan> findAllByMemberAndEndDateBeforeAndDeletedAtIsNull(Member member, LocalDate today, Pageable pageable);
    // 오늘을 포함하여 지나지 않은 계획들
    Page<Plan> findAllByMemberAndEndDateGreaterThanEqualAndDeletedAtIsNull(Member member, LocalDate today, Pageable pageable);
    @Query("SELECT p FROM Plan p " +
            "LEFT JOIN PlanReview pr ON p.planId = pr.plan.planId " +
            "WHERE p.endDate < :now " +
            "AND p.isPublic = true " +
            "AND p.deletedAt IS NULL " +
            "AND (pr.report IS NULL OR pr.report = false)")
    Page<Plan> findOpenPlans(@Param("now") LocalDate now, Pageable pageable);

}
