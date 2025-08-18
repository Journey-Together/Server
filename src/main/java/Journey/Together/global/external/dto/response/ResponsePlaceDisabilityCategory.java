package Journey.Together.global.external.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResponsePlaceDisabilityCategory(
	String parking,
	String route,
	String publictransport,
	String ticketoffice,
	String promotion,
	String wheelchair,
	String elevator,
	String restroom,
	String auditorium,
	String room,
	String handicapetc,
	String braileblock,
	String helpdog,
	String guidehuman,
	String audioguide,
	String bigprint,
	String brailepromotion,
	String guidesystem,
	String blindhandicapetc,
	String signguide,
	String videoguide,
	String hearingroom,
	String hearinghandicapetc,
	String stroller,
	String lactationroom,
	String babysparechair,
	String infantsfamilyetc
) {}