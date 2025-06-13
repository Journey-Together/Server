package Journey.Together.global.external.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResponseCode(
	String lDongSignguNm,
	String rnum,
	String code,
	String name,
	String lDongRegnCd,
	String lDongRegnNm,
	String lDongSignguCd
) {
}
