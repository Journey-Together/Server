package Journey.Together.domain.plan.repository;

import Journey.Together.domain.plan.entity.Day;
import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DayRepository extends JpaRepository<Day,Long> {

    void deleteAllByMemberAndPlan (Member member, Plan plan);
    List<Day> findByPlanOrderByCreatedAtDesc(Plan plan);
    List<Day> findAllByMemberAndPlanOrderByDateAsc(Member member, Plan plan);
}
