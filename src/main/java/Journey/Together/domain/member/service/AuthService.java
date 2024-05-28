package Journey.Together.domain.member.service;

import Journey.Together.domain.member.dto.LoginRes;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.enumerate.LoginType;
import Journey.Together.domain.member.enumerate.MemberType;
import Journey.Together.domain.member.repository.MemberRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.security.kakao.KakaoClient;
import Journey.Together.global.security.kakao.dto.KakaoProfile;
import Journey.Together.global.security.jwt.TokenProvider;
import Journey.Together.global.security.jwt.dto.TokenDto;
import Journey.Together.global.security.kakao.dto.KakaoToken;
import Journey.Together.global.security.naver.dto.NaverProperties;
import Journey.Together.global.security.naver.dto.NaverUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final KakaoClient kakaoClient;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public LoginRes signIn(String token, String type) {
        Member member = null;
        TokenDto tokenDto = null;

        if(type == "KAKAO") {
            //Business Logic
            // 카카오로 액세스 토큰 요청하기
            KakaoToken kakaoAccessToken = kakaoClient.getKakaoAccessToken(token);
            // 카카오톡에 있는 사용자 정보 반환
            KakaoProfile kakaoProfile = kakaoClient.getMemberInfo(kakaoAccessToken);
            // 반환된 정보의 이메일 기반으로 사용자 테이블에서 계정 정보 조회 진행
            member = memberRepository.findMemberByEmailAndDeletedAtIsNull(kakaoProfile.kakao_account().email()).orElse(null);
            // 이메일 존재 시 로그인 , 존재하지 않을 경우 회원가입 진행
            if (member == null) {
                Member newMember = Member.builder()
                        .email(kakaoProfile.kakao_account().email())
                        .name(kakaoProfile.kakao_account().profile().nickname())
                        .memberType(MemberType.GENERAL)
                        .loginType(LoginType.KAKAO)
                        .build();

                member = memberRepository.save(newMember);
            }
            tokenDto = tokenProvider.createToken(member);

            // RefreshToken 저장
            member.setRefreshToken(tokenDto.refreshToken());

            // Response
            return LoginRes.of(member, tokenDto);

        } else if (type.equals("NAVER")) {
            NaverUserResponse.NaverUserDetail naverProfile = toRequestProfile(token.substring(7));
            member = memberRepository.findMemberByEmailAndDeletedAtIsNull(naverProfile.getEmail()).orElse(null);

            if (member == null) {
                Member newMember = Member.builder()
                        .email(naverProfile.getEmail() != null ? naverProfile.getEmail() : "Unknown")
                        .profileUrl(naverProfile.getProfile_image() != null ? naverProfile.getProfile_image() : "Unknown")
                        .name(naverProfile.getName() != null ? naverProfile.getName() : "Unknown")
                        .memberType(MemberType.GENERAL)
                        .loginType(LoginType.NAVER)
                        .build();

                member = memberRepository.save(newMember);
            }

            tokenDto = tokenProvider.createToken(member);
            member.setRefreshToken(tokenDto.refreshToken());

        }
        return LoginRes.of(member, tokenDto);
    }


    private NaverUserResponse.NaverUserDetail toRequestProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<NaverUserResponse> response =
                restTemplate.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.GET, request, NaverUserResponse.class);

        return response.getBody().getNaverUserDetail();
    }


    public void signOut(String token, Member member) {
        // Validation
        String accessToken = token.substring(7);
        tokenProvider.validateToken(accessToken);

        // Business Logic - Refresh Token 삭제 및 Access Token 블랙리스트 등록
        String key = member.getEmail();
//        redisClient.deleteValue(key);
//        redisClient.setValue(accessToken, "logout", tokenProvider.getExpiration(accessToken));
        member.setRefreshToken(null);

        // Response
    }

    @Transactional
    public void withdrawal(Member member) {
        // Validation

        // Business Logic - 회원 논리적 삭제 진행
        memberRepository.delete(member);

        // Response
    }

    public TokenDto reissue(String token, Member member) {
        // Validation - RefreshToken 유효성 검증
        String refreshToken = token.substring(7);
        tokenProvider.validateToken(refreshToken);
        String memberRefreshToken = member.getRefreshToken();
        // 입력받은 refreshToken과 Redis의 RefreshToken 간의 일치 여부 검증
        if(refreshToken.isBlank() || memberRefreshToken.isEmpty() || !memberRefreshToken.equals(refreshToken)) {
            throw new ApplicationException(ErrorCode.WRONG_TOKEN_EXCEPTION);
        }

        // Business Logic & Response - Access Token 새로 발급 + Refresh Token의 유효 기간이 Access Token의 유효 기간보다 짧아졌을 경우 Refresh Token도 재발급
        return tokenProvider.reissue(member, refreshToken);
    }
}
