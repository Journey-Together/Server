package Journey.Together.global.external.kakao;

import Journey.Together.domain.place.service.kakao.dto.KakaoAddress;
import Journey.Together.domain.place.service.kakao.dto.KakaoKeyword;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakaoMapOpenAPI", url = "${spring.api.kakao.map-search-address}")
public interface KakaoMapOpenApiClient {
    @GetMapping(value = "/address.{format}", consumes = MediaType.APPLICATION_JSON_VALUE)
    KakaoAddress inquireByAddress(@RequestHeader("Authorization") String authorization,
                                  @PathVariable("format") String format,
                                  @RequestParam("query") String address);

    @GetMapping(value = "/keyword.{format}", consumes = MediaType.APPLICATION_JSON_VALUE)
    KakaoKeyword inquireByKeyword(@RequestHeader("Authorization") String authorization,
                                  @PathVariable("format") String format,
                                  @RequestParam("query") String address);
}
