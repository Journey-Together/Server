package Journey.Together.domain.place.service.kakao;

import Journey.Together.domain.place.service.kakao.dto.KakaoAddress;
import Journey.Together.domain.place.service.kakao.dto.KakaoKeyword;
import Journey.Together.global.external.kakao.KakaoMapOpenApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;

@Service
@RequiredArgsConstructor
public class KakaoApiService {
    private final KakaoMapOpenApiClient client;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    /**
     * 주소 기반 api 요청
     */
    public KakaoAddress getPlaceInfoByAddress(String address, @Nullable String format) {
        String authorization = "KakaoAK " + kakaoClientId;
        String fmt = (format == null || format.isBlank()) ? "json" : format;

        return client.inquireByAddress(authorization, fmt, address);
    }

    /**
     * 키워드 기반 api 요청
     */
    public KakaoKeyword getPlaceInfoByKeyword(String keyword, @Nullable String format) {
        String authorization = "KakaoAK " + kakaoClientId;
        String fmt = (format == null || format.isBlank()) ? "json" : format;

        return client.inquireByKeyword(authorization, fmt, keyword);
    }
}
