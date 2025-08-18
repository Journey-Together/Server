package Journey.Together.global.external.dto.request;

import java.util.HashMap;
import java.util.Map;

public record RequestCode(
	String serviceKey,
	String MobileOS,
	String MobileApp,
	String _type,
	int numOfRows,
	int pageNo
){
	public static RequestCode of(String serviceKey, int numOfRows, int pageNo){
		return new RequestCode(serviceKey, "AND","다온길","json", numOfRows, pageNo);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("serviceKey", serviceKey);
		map.put("MobileOS", MobileOS);
		map.put("MobileApp", MobileApp);
		map.put("_type", _type);
		map.put("numOfRows", numOfRows);
		map.put("pageNo", pageNo);
		return map;
	}
}
