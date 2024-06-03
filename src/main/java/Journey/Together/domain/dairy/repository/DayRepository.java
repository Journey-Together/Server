package Journey.Together.domain.dairy.repository;

import Journey.Together.domain.dairy.entity.Day;
import Journey.Together.domain.dairy.entity.Plan;
import Journey.Together.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DayRepository extends JpaRepository<Day,Long> {

    void deleteAllByMemberAndPlan (Member member, Plan plan);
}
