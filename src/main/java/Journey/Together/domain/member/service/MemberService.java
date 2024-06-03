package Journey.Together.domain.member.service;

import Journey.Together.domain.member.dto.MyPageRes;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MyPageRes getMypage(Member member){
        Long date = Duration.between(member.getCreatedAt(), LocalDateTime.now()).toDays();
        return new MyPageRes(member.getName(), 0, date, member.getProfileUrl());
    }

}
