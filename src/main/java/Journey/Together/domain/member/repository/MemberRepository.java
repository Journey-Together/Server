package Journey.Together.domain.member.repository;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.enumerate.MemberType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByEmailAndDeletedAtIsNull(String email);

    Optional<Member> findMemberByEmailAndMemberTypeAndDeletedAtIsNull(String email, MemberType memberType);
}
