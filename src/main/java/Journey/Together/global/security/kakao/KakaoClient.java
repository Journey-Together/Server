package Journey.Together.global.security.kakao;

import Journey.Together.global.security.kakao.dto.KakaoProfile;
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
    private String kakwaoGrantType;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Value("${spring.security.oauth2.client.admin-key}")
    private String adminKey;

    @Value("${spring.security.oauth2.client.withdrawal.unlink-url}")
    private String unlinkUri;

    public KakaoProfile getMemberInfo(String accesToken) {
        // 요청 기본 객체 생성
        WebClient webClient = WebClient.create(kakaoUserInfoUri);
        // 요청 보내서 응답 받기
        String response = webClient.post()
                .uri(kakaoUserInfoUri)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .header("Authorization", accesToken)
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

    //카카오와 연결 끊기
    public String unlinkUser(Long userId){
        // 요청 기본 객체 생성
        WebClient webClient = WebClient.create(unlinkUri);
        // 요청 보내서 응답 받기
        String response = webClient.post()
                .uri(unlinkUri)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .header("Authorization", "KakaoAK " + adminKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        // 수신된 응답 Mapping
        ObjectMapper objectMapper = new ObjectMapper();

        return response;
    }
//    public String unlinkUser(Long userId) {
//
//        URI uri = URI.create(KAKAO_UNLINK_URL + "?target_id_type=user_id&target_id=" + userId);
//
//        RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.POST, uri);
//        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
//
//    }
}
