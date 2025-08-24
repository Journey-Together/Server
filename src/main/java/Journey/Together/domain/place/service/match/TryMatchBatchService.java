package Journey.Together.domain.place.service.match;

import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.enumerated.MatchStatus;
import Journey.Together.domain.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TryMatchBatchService {
    private final PlaceRepository placeRepository;
    private final PlaceMatchingService matching; // 네가 만든 서비스 (phoneMatch 제외 버전)

    /**
     * DB에서 최대 limit개 로드 후 tryMatch 실행 (부작용 포함).
     * - onlyActive=true면 is_active=true인 것만
     * - 정렬: id ASC (원하면 바꿔도 됨)
     */
    public RunResult run(int limit, boolean onlyActive) {
        var pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "id"));
        List<Place> batch = (onlyActive
                ? placeRepository.findByIsActiveTrue(pageable)
                : placeRepository.findAll(pageable)
        ).getContent();

        Map<MatchStatus, Integer> counts = new EnumMap<>(MatchStatus.class);
        List<Long> notFoundIds = new ArrayList<>();

        for (Place p : batch) {
            try {
                var d = matching.tryMatch(p); // 부작용 O: 이슈 저장/비활성화 실행
                counts.merge(d.status(), 1, Integer::sum);
                if (d.status() == MatchStatus.NOT_FOUND) {
                    notFoundIds.add(p.getId());
                }
                log.info("[tryMatch] placeId={} status={} score={} dist={}m nameSim={} tok={} addrSim={}",
                        p.getId(), d.status(), fmt(d.finalScore()), fmt(d.distMeters()),
                        fmt(d.nameSim()), fmt(d.tokenOverlap()), fmt(d.addrSim()));
            } catch (Exception e) {
                log.warn("[tryMatch] failed placeId={} err={}", p.getId(), e.toString());
            }
        }

        return new RunResult(batch.size(), counts, notFoundIds);
    }

    // ===== DTO =====
    public record RunResult(int processed,
                            Map<MatchStatus, Integer> counts,
                            List<Long> notFoundPlaceIds) {}

    private static String fmt(double v) { return Double.isNaN(v) ? "-" : String.format("%.1f", v); }
}
