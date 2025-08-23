package Journey.Together.domain.place.service.match;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceMatchingService {
    private static final double NAME_SIM_EXISTS = 0.82;
    private static final double TOKEN_OVERLAP_EXISTS = 0.60;
    private static final double ADDR_SIM_STRONG = 0.60;
    private static final double ADDR_SIM_WEAK = 0.50;
    private static final double RENAME_NAME_SIM_MAX = 0.60;
    private static final int SAME_PLACE_DIST_STRONG = 50;     // m
    private static final int SAME_PLACE_DIST_SOFT   = 100;    // m
    private static final int MOVED_DIST_MIN         = 100;    // m
    private static final int MOVED_DIST_MAX         = 1500;   // m
    private static final int SCAN_RADIUS_MAX        = 1500;   // m (없음 판정 반경 상한)
    private static final int GLOBAL_FILTER_RADIUS   = 3000;   // m (전역 키워드 노이즈 억제)
}
