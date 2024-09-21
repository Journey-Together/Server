package Journey.Together.global.security.naver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NaverDeleteResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("result")
    private String result;
    @JsonProperty("error")
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;

}