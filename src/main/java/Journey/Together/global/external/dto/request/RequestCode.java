package Journey.Together.global.external.dto.request;

public record RequestCode(
	String serviceKey,
	String MobileOs,
	String MobileApp,
	String _type,
	int numOfRows,
	int pageNo
){
	public static RequestCode of(String serviceKey, int numOfRows, int pageNo){
		return new RequestCode(serviceKey, "AND","다온길","json", numOfRows, pageNo);
	}
}
