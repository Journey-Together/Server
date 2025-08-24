package Journey.Together.domain.place.service.match;

import Journey.Together.domain.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceMatchingSyncService {
    private final PlaceRepository placeRepository;
    private final TryMatchBatchService tryMatchBatchService;

    /**
     * 매달 1일 00:00 (KST) 실행
     * 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 0 1 * ?", zone = "Asia/Seoul")
    public void runMonthly() {
        int limit = (int) placeRepository.count();
        boolean onlyActive = true; // 기본값 유지
        var result = tryMatchBatchService.run(limit, onlyActive);
        log.info("[MonthlyMatchJob] processed={}, counts={}, notFoundIds={}",
                result.processed(), result.counts(), result.notFoundPlaceIds());
    }
}
