package Journey.Together.domain.place.service.kakao.dto;

import java.util.List;

public record KakaoKeyword(
        Meta meta,
        List<Documents> documents
) {
    public record Meta(
            SameName same_name,
            int pageable_count,
            int total_count,
            boolean is_end
    ){
        public record SameName(
                List<String> region,
                String keyword,
                String selected_region
        ) {
        }
    }
    public record Documents(
            String place_name,
            String distance,
            String place_url,
            String category_name,
            String address_name,
            String road_address_name,
            String id,
            String phone,
            String category_group_code,
            String category_group_name,
            String y,
            String x
    ){
    }
}
