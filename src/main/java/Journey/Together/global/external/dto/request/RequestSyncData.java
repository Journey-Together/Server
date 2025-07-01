package Journey.Together.global.external.dto.request;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public record RequestSyncData(
	String MobileOS,
	String MobileApp,
	String serviceKey,
	String _type, // json
	String modifiedtime,
	Integer numOfRows,

	Integer pageNo
) {
	public static RequestSyncData of(LocalDate modifiedTime, String serviceKey, Integer numOfRows,
		Integer pageNo){
		return new RequestSyncData("AND", "다온길",serviceKey, "json", modifiedTime.format(
			DateTimeFormatter.ofPattern("yyyyMMdd")), numOfRows, pageNo);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("serviceKey", serviceKey);
		map.put("MobileOS", MobileOS);
		map.put("MobileApp", MobileApp);
		map.put("_type", _type);
		map.put("numOfRows", numOfRows);
		map.put("pageNo", pageNo);
		map.put("modifiedtime", modifiedtime);
		return map;
	}
}
