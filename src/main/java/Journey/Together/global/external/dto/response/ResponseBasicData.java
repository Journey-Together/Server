package Journey.Together.global.external.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResponseBasicData(
	String addr1,
	String addr2 ,
	Long contentid,
	String createdtime,
	String firstimage,
	Double mapx,
	Double mapy,
	String tel,
	String sigungucode,
	String title,
	String cat1,
	String areacode
) {
}
