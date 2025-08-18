package Journey.Together.domain.place.service.kakao;

import Journey.Together.domain.place.service.kakao.dto.KakaoAddress;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceSearchClient {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.api.kakao.map-search-address}")
    private String kakaoMapSearchUri;


    /**
     *
     * @param searchAddress 검색하려는 주소
     * @param format 응답형식
     * @return
     */
    public KakaoAddress getPlaceInfo(String searchAddress, @Nullable String format) {
        // 요청 보낼 객체 기본 생성
        WebClient webClient = WebClient.builder()
                .baseUrl(kakaoMapSearchUri)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoClientId)
                .build();

        String fmt = (format == null || format.isBlank()) ? "json" : format;

        // 요청 보내기 및 응답 수신
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/address.{format}")
                        .queryParam("query", searchAddress)    // 필수 쿼리 파라미터
                        // .queryParam("x", 127.1086228)
                        // .queryParam("y", 37.4012191)
                        // .queryParam("radius", 20000)
                        .build(fmt))
                .retrieve()
                .onStatus(HttpStatusCode::isError, r ->
                        r.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new IllegalStateException("Kakao API error: " + r.statusCode() + " - " + body))
                        )
                )
                .bodyToMono(String.class)
                .block();

        // 수신된 응답 Mapping
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoAddress kakaoAddress;
        try {
            kakaoAddress = objectMapper.readValue(response, KakaoAddress.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return kakaoAddress;
    }
}
