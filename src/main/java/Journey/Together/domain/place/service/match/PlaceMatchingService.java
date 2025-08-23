package Journey.Together.domain.place.service.match;

import Journey.Together.domain.place.dto.match.Candidate;
import Journey.Together.domain.place.dto.match.MatchDecision;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.entity.PlaceMatchIssue;
import Journey.Together.domain.place.enumerated.MatchStatus;
import Journey.Together.domain.place.repository.PlaceMatchIssueRepository;
import Journey.Together.domain.place.service.kakao.PlaceSearchClient;
import Journey.Together.domain.place.util.MatchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceMatchingService {
    private final PlaceSearchClient kakao;
    private final PlaceMatchIssueRepository issueRepo;
    private final MatchUtils U;
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

    //데이터 저장 및 헬퍼 메소드
    private void saveIssue(Place place, String kakaoAddress, Double score, MatchStatus status) {
        PlaceMatchIssue issue = PlaceMatchIssue.builder()
                .place(place)
                .kakaoAddress(kakaoAddress)
                .score(score)
                .matchStatus(status)
                .build();
        issueRepo.save(issue);
    }
    private void deactivate(Place p) {
        try { if (p.isActive()) p.setIsActive(); } catch (Exception ignored) {}
    }
    private MatchDecision toDecision(MatchStatus st, Scored s, boolean rename, boolean moved) {
        return new MatchDecision(
                st, s.finalScore(),
                s.best() != null ? s.best().name()    : null,
                s.best() != null ? s.best().address() : null,
                s.best() != null ? s.best().sourceId(): null,
                s.best() != null ? s.best().lon()     : null,
                s.best() != null ? s.best().lat()     : null,
                s.nameSim(), s.tokenOverlap(), s.addrSim(),
                s.distMeters(), s.distScore(),
                rename, moved, s.phoneMatch()
        );
    }
    private static Double parseDouble(String s) {
        try {
            return s==null?null:Double.parseDouble(s);
        } catch (Exception e) {
            return null;
        }
    }
    private double minDistance(List<Coord> anchors, Double lon, Double lat) {
        if (lon==null || lat==null) return Double.NaN;
        double best = Double.POSITIVE_INFINITY;
        for (Coord a : anchors) {
            if (a.lon==null || a.lat==null) continue;
            double m = U.distanceMeters(a.lon, a.lat, lon, lat);
            if (!Double.isNaN(m) && m < best) best = m;
        }
        return best==Double.POSITIVE_INFINITY ? Double.NaN : best;
    }
    private static String nullToEmpty(String s) {
        return s==null?"":s;
    }
    private static String fmt(Double d) {
        return d==null?"-":String.format("%.1f",d);
    }

    //내부 record
    private record Coord(Double lon, Double lat) {}
    private record Retrieval(List<Candidate> all, int addrDocCount) {}
    private record Scored(
            Candidate best,
            double nameSim,
            double tokenOverlap,
            double addrSim,
            double distMeters,
            double distScore,
            double finalScore,
            boolean renameSuspect,
            boolean movedSuspect,
            boolean phoneMatch
    ) {}
}
