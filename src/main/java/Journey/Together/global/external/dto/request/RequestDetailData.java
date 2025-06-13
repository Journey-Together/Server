package Journey.Together.global.external.dto.request;

import java.util.HashMap;
import java.util.Map;

public record RequestDetailData(
	String MobileOS,
	String MobileApp,
	String serviceKey,
	String _type, // json
	Long contentId
) {
	public static RequestDetailData of(String serviceKey, Long contentId){
		return new RequestDetailData("AND", "다온길", serviceKey, "json", contentId);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("serviceKey", serviceKey);
		map.put("MobileOS", MobileOS);
		map.put("MobileApp", MobileApp);
		map.put("_type", _type);
		map.put("contentId", contentId);
		return map;
	}
}
