package Journey.Together.domain.place.dto.match;

import lombok.Builder;

@Builder
public record Candidate(
        String source,    // "ADDR" | "KW"
        String sourceId,   // Kakao id(키워드) or "addr:<query>"
        String name,       // place_name (KW) or null
        String address,    // road_address_name 우선, 없으면 address_name
        Double lon,        // x
        Double lat,        // y
        String phone,      // keyword만 제공됨
        String placeUrl   // keyword만
) {
}
