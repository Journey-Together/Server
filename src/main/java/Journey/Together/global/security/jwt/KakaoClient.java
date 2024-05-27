package Journey.Together.global.security.jwt;

import Journey.Together.global.security.kakao.dto.KakaoToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoClient {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String kakaoGrantType;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    /**
     * 카카오 서버에 인가코드 기반으로 사용자의 토큰 정보를 조회하는 메소드
     * @param code - 카카오에서 발급해준 인가 코드
     * @return - 카카오에서 반환한 응답 토큰 객체
     */

    public KakaoToken getKakaoAccessToken(String code) {
        // 요청 보낼 객체 기본 생성
        WebClient webClient = WebClient.create(kakaoTokenUri);

        //요청 본문
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", kakaoGrantType);
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        params.add("client_secret", kakaoClientSecret);

        // 요청 보내기 및 응답 수신
        String response = webClient.post()
                .uri(kakaoTokenUri)
                .header("Content-type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(params))
                .retrieve() // 데이터 받는 방식, 스프링에서는 exchange는 메모리 누수 가능성 때문에 retrieve 권장
                .bodyToMono(String.class) // (Mono는 단일 데이터, Flux는 복수 데이터)
                .block();// 비동기 방식의 데이터 수신

        // 수신된 응답 Mapping
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoToken kakaoToken;
        try {
            kakaoToken = objectMapper.readValue(response, KakaoToken.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return kakaoToken;
    }

    public KakaoProfile getMemberInfo(KakaoToken kakaoToken) {
        // 요청 기본 객체 생성
        WebClient webClient = WebClient.create(kakaoUserInfoUri);

        // 요청 보내서 응답 받기
        String response = webClient.post()
                .uri(kakaoUserInfoUri)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .header("Authorization", "Bearer " + kakaoToken.access_token())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // 수신된 응답 Mapping
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile;
        try {
            kakaoProfile = objectMapper.readValue(response, KakaoProfile.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return kakaoProfile;
    }
}
