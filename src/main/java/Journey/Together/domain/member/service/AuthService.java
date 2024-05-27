package Journey.Together.domain.member.service;

import Journey.Together.domain.member.dto.LoginRes;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.enumerate.LoginType;
import Journey.Together.domain.member.repository.MemberRepository;
import Journey.Together.global.security.jwt.KakaoClient;
import Journey.Together.global.security.jwt.KakaoProfile;
import Journey.Together.global.security.jwt.TokenProvider;
import Journey.Together.global.security.jwt.dto.TokenDto;
import Journey.Together.global.security.kakao.dto.KakaoToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final KakaoClient kakaoClient;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @Transactional
    public LoginRes signIn(String code, LoginType type) {
        //Business Logic
        // 카카오로 액세스 토큰 요청하기
        KakaoToken kakaoAccessToken = kakaoClient.getKakaoAccessToken(code);
        // 카카오톡에 있는 사용자 정보 반환
        KakaoProfile kakaoProfile = kakaoClient.getMemberInfo(kakaoAccessToken);
        // 반환된 정보의 이메일 기반으로 사용자 테이블에서 계정 정보 조회 진행
        Member member = memberRepository.findMemberByEmailAndDeletedAtIsNull(kakaoProfile.kakao_account().email()).orElse(null);
        // 이메일 존재 시 로그인 , 존재하지 않을 경우 회원가입 진행
        if(member == null) {
            Member newMember = Member.builder()
                    .email(kakaoProfile.kakao_account().email())
                    .name(kakaoProfile.kakao_account().profile().nickname())
                    .memberType("GENERAL")
                    .loginType("KAKAO")
                    .build();

            member = memberRepository.save(newMember);
        }
        TokenDto tokenDto = tokenProvider.createToken(member);

        // RefreshToken 저장
        member.setRefreshToken(kakaoAccessToken.refresh_token());

        // Response
        return LoginRes.of(member, tokenDto);

    }
}
