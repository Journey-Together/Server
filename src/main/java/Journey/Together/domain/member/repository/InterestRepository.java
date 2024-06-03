package Journey.Together.domain.member.repository;

import Journey.Together.domain.member.entity.Interest;
import Journey.Together.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest,Long> {
    Interest findByMemberAndDeletedAtIsNull(Member member);

}
