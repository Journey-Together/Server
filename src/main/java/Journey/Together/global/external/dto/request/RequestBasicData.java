package Journey.Together.global.external.dto.request;

public record RequestBasicData(
	String MobileOs,
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
}
