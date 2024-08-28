package Journey.Together.domain.member.service;

import Journey.Together.domain.member.dto.LoginRes;
import Journey.Together.domain.member.entity.Interest;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.enumerate.LoginType;
import Journey.Together.domain.member.enumerate.MemberType;
import Journey.Together.domain.member.repository.InterestRepository;
import Journey.Together.domain.member.repository.MemberRepository;
import Journey.Together.global.common.CustomMultipartFile;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.security.kakao.KakaoClient;
import Journey.Together.global.security.kakao.dto.KakaoProfile;
import Journey.Together.global.security.jwt.TokenProvider;
import Journey.Together.global.security.jwt.dto.TokenDto;
import Journey.Together.global.security.naver.dto.NaverUserResponse;
import Journey.Together.global.util.S3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final KakaoClient kakaoClient;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final S3Client s3Client;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public LoginRes signIn(String token, String type) throws IOException {
        Member member = null;
        TokenDto tokenDto = null;

        if(type.equals("KAKAO")) {
            //Business Logic
            // 카카오톡에 있는 사용자 정보 반환
            KakaoProfile kakaoProfile = kakaoClient.getMemberInfo(token);
            // 반환된 정보의 이메일 기반으로 사용자 테이블에서 계정 정보 조회 진행
            member = memberRepository.findMemberByEmailAndDeletedAtIsNull(kakaoProfile.kakao_account().email()).orElse(null);
            // 이메일 존재 시 로그인 , 존재하지 않을 경우 회원가입 진행
            if(member == null) {
                MultipartFile imageFile = convertUrlToMultipartFile(kakaoProfile.kakao_account().profile().profile_image_url());
                String uuid = s3Client.createFolder();
                s3Client.upload(imageFile,uuid,"profile_"+uuid);
                Member newMember = Member.builder()
                        .email(kakaoProfile.kakao_account().email())
                        .name(kakaoProfile.kakao_account().name())
                        .nickname(kakaoProfile.kakao_account().profile().nickname())
                        .profileUuid(uuid)
                        .phone(null)
                        .memberType(MemberType.valueOf("GENERAL"))
                        .loginType(LoginType.valueOf("KAKAO"))
                        .build();
                member = memberRepository.save(newMember);
                Interest interest = Interest.builder()
                        .member(member)
                        .isHear(false)
                        .isChild(false)
                        .isPhysical(false)
                        .isVisual(false)
                        .isElderly(false)
                        .build();
                interestRepository.save(interest);
            }
            tokenDto = tokenProvider.createToken(member);
            member.setRefreshToken(tokenDto.refreshToken());

            // Response
            return LoginRes.of(member, tokenDto);

        } else if (type.equals("NAVER")) {
            NaverUserResponse.NaverUserDetail naverProfile = toRequestProfile(token.substring(7));
            member = memberRepository.findMemberByEmailAndDeletedAtIsNull(naverProfile.getEmail()).orElse(null);

            if (member == null) {
                MultipartFile imageFile = convertUrlToMultipartFile(naverProfile.getProfile_image() != null ? naverProfile.getProfile_image() : null);
                String uuid = s3Client.createFolder();
                s3Client.upload(imageFile,uuid,"profile_"+uuid);

                Member newMember = Member.builder()
                        .email(naverProfile.getEmail() != null ? naverProfile.getEmail() : "Unknown")
                        .profileUuid(uuid)
                        .name(naverProfile.getName() != null ? naverProfile.getName() : "Unknown")
                        .nickname(naverProfile.getNickname() != null ? naverProfile.getNickname() : "Unknown")
                        .memberType(MemberType.GENERAL)
                        .loginType(LoginType.NAVER)
                        .build();

                member = memberRepository.save(newMember);
                Interest interest = Interest.builder()
                        .member(member)
                        .isHear(false)
                        .isChild(false)
                        .isPhysical(false)
                        .isVisual(false)
                        .isElderly(false)
                        .build();
                interestRepository.save(interest);
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

    @Transactional
    public void signOut(String token, Member member) {
        // Validation
        String accessToken = token.substring(7);
        tokenProvider.validateToken(accessToken);

        // Business Logic - Refresh Token 삭제 및 Access Token 블랙리스트 등록
        tokenProvider.getExpiration(accessToken);
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
    @Transactional
    public TokenDto reissue(String token, Member member) {
        String refreshToken = token.substring(7);

        // Token 유효성 검사
        tokenProvider.validateToken(refreshToken);

        String memberRefreshToken = member.getRefreshToken();
        if (refreshToken.isBlank() || memberRefreshToken.isEmpty() || !memberRefreshToken.equals(refreshToken)) {
            throw new ApplicationException(ErrorCode.WRONG_TOKEN_EXCEPTION);
        }

        // 토큰 재발급
        TokenDto tokenDto = tokenProvider.reissue(member, refreshToken);

        // 새로운 리프레시 토큰을 DB에 저장
        member.setRefreshToken(tokenDto.getRefreshToken());
        memberRepository.save(member);

        return tokenDto;
    }


    //url->multipartFile로 변환
    private MultipartFile convertUrlToMultipartFile(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);

        try(InputStream inputStream = url.openStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            // 1) image url -> byte[]
            BufferedImage urlImage = ImageIO.read(inputStream);
            ImageIO.write(urlImage, "jpg", bos);
            byte[] byteArray = bos.toByteArray();
            // 2) byte[] -> MultipartFile
            MultipartFile multipartFile = new CustomMultipartFile(byteArray, imageUrl);
            return multipartFile; // image를 storage에 저장하는 메서드 호출
        }
    }
}
