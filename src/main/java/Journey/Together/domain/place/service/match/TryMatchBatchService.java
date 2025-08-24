package Journey.Together.domain.place.service.match;

import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.entity.PlaceMatchIssue;
import Journey.Together.domain.place.enumerated.MatchStatus;
import Journey.Together.domain.place.repository.PlaceMatchIssueRepository;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.global.common.discord.DiscordMatchNotifitionSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TryMatchBatchService {
    private static final int SUMMARY_MAX_ITEMS = 30;

    private final PlaceRepository placeRepository;
    private final PlaceMatchingService matching;
    private final PlaceMatchIssueRepository issueRepo;
    private final DiscordMatchNotifitionSender discordSender;

    /**
     * DB에서 최대 limit개 로드 후 tryMatch 실행 (부작용 포함).
     * - 정렬: id ASC (원하면 바꿔도 됨)
     */
    public RunResult run(int limit, boolean onlyActive) {
        LocalDateTime from = LocalDateTime.now();

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

        LocalDateTime to = LocalDateTime.now();
        sendDiscordSummary(from, to);

        return new RunResult(batch.size(), counts, notFoundIds);
    }

    private void sendDiscordSummary(LocalDateTime from, LocalDateTime to) {
        // 상태별 개수
        long needReviewCount = issueRepo.countByMatchStatusAndMatchedAtBetween(MatchStatus.NEED_REVIEW, from, to);
        long conflictCount   = issueRepo.countByMatchStatusAndMatchedAtBetween(MatchStatus.CONFLICT,   from, to);
        long notFoundCount   = issueRepo.countByMatchStatusAndMatchedAtBetween(MatchStatus.NOT_FOUND,  from, to);

        // 아무 것도 없으면 전송 생략 (스팸 방지)
        if (needReviewCount + conflictCount + notFoundCount == 0) return;

        // 상태별 최신 N건만 노출
        PageRequest topN = PageRequest.of(0, SUMMARY_MAX_ITEMS);
        List<PlaceMatchIssue> needReviewItems =
                issueRepo.findByMatchStatusAndMatchedAtBetweenOrderByMatchedAtDesc(MatchStatus.NEED_REVIEW, from, to, topN);
        List<PlaceMatchIssue> conflictItems =
                issueRepo.findByMatchStatusAndMatchedAtBetweenOrderByMatchedAtDesc(MatchStatus.CONFLICT, from, to, topN);
        List<PlaceMatchIssue> notFoundItems =
                issueRepo.findByMatchStatusAndMatchedAtBetweenOrderByMatchedAtDesc(MatchStatus.NOT_FOUND, from, to, topN);

        discordSender.sendMatchSummary("매칭 결과 요약 ",
                needReviewCount, needReviewItems,
                conflictCount,   conflictItems,
                notFoundCount,   notFoundItems);
    }

    // ===== DTO =====
    public record RunResult(int processed,
                            Map<MatchStatus, Integer> counts,
                            List<Long> notFoundPlaceIds) {}

    private static String fmt(double v) { return Double.isNaN(v) ? "-" : String.format("%.1f", v); }
}
