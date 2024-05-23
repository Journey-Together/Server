package Journey.Together.domain.member.repository;

import Journey.Together.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {

}
