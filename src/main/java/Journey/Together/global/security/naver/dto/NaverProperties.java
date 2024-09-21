package Journey.Together.global.security.naver.dto;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@Getter
@ConfigurationProperties(prefix = "naver")
public class NaverProperties {
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}")
    private String naverGrantType;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String naverUserInfoUri;

    // 새로운 액세스 토큰을 요청하기 위한 URI 생성
    public String refreshTokenUri(String refreshToken) {
        return UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/token")
                .queryParam("grant_type", "refresh_token")
                .queryParam("client_id", naverClientId)
                .queryParam("client_secret", naverClientSecret)
                .queryParam("refresh_token", refreshToken)
                .toUriString();
    }

    public String delete(String accessToken) {
        return UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/token")
                .queryParam("grant_type", "delete")
                .queryParam("client_id", naverClientId)
                .queryParam("client_secret", naverClientSecret)
                .queryParam("access_token", accessToken)
                .queryParam("service_provider", "NAVER")
                .toUriString();
    }


}
