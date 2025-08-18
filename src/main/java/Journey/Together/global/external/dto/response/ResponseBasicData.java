package Journey.Together.global.external.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import Journey.Together.domain.place.entity.Place;

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
	public static Place mapToEntity(ResponseBasicData basic) {
		return Place.builder()
			.id(basic.contentid())
			.name(basic.title())
			.address(basic.addr1() + " " + basic.addr2())
			.firstImg(basic.firstimage())
			.category(basic.cat1())
			.mapX(basic.mapx())
			.mapY(basic.mapy())
			.createdAt(basic.createdtime())
			.areaCode(basic.areacode())
			.sigunguCode(basic.sigungucode())
			.tel(basic.tel()).build();
	}
}
