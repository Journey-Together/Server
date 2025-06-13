package Journey.Together.global.external.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResponseDataDetail(
	String overview,
	String homepage
) {
}
