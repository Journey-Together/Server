package Journey.Together.global.external.dto.request;

public record RequestDetailData(
	String MobileOs,
	String MobileApp,
	String serviceKey,
	String _type, // json
	Long contentId
) {
	public static RequestDetailData of(String serviceKey, Long contentId){
		return new RequestDetailData("AND", "다온길", serviceKey, "json", contentId);
	}
}
