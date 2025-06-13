package Journey.Together.global.external.dto.request;

import java.util.HashMap;
import java.util.Map;

public record RequestBasicData(
	String MobileOS,
	String MobileApp,
	String serviceKey,
	String _type, // json
	String lDongRegnCd, //법정동 시도 코드
	String lclsSystm1, //분류체계
	Integer numOfRows,

	Integer pageNo
) {
	public static RequestBasicData of(String lDongRegnCd, String lclsSystm1, String serviceKey, Integer numOfRows,
		Integer pageNo){
		return new RequestBasicData("AND", "다온길",serviceKey, "json", lDongRegnCd, lclsSystm1, numOfRows, pageNo);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("serviceKey", serviceKey);
		map.put("MobileOS", MobileOS);
		map.put("MobileApp", MobileApp);
		map.put("_type", _type);
		map.put("numOfRows", numOfRows);
		map.put("pageNo", pageNo);
		map.put("lDongRegnCd", lDongRegnCd);
		map.put("lclsSystm1", lclsSystm1);
		return map;
	}
}
