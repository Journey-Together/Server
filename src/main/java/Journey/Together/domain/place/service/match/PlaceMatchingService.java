package Journey.Together.domain.place.service.match;

import Journey.Together.domain.place.dto.match.Candidate;
import Journey.Together.domain.place.dto.match.MatchDecision;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.entity.PlaceMatchIssue;
import Journey.Together.domain.place.enumerated.MatchStatus;
import Journey.Together.domain.place.repository.PlaceMatchIssueRepository;
import Journey.Together.domain.place.service.kakao.KakaoApiService;
import Journey.Together.domain.place.service.kakao.dto.KakaoAddress;
import Journey.Together.domain.place.service.kakao.dto.KakaoKeyword;
import Journey.Together.domain.place.util.MatchUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceMatchingService {
    private final KakaoApiService kakao;
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
    private static final int AREA_MATCH_DIST_MAX  = 3000; // 영역형 POI 존재 인정 거리(3km)
    private static final int AREA_RELATED_RADIUS  = 4000; // 영역형 관련성 탐색 반경(4km)
    private static final double ADDR_SIM_RELATED  = 0.75; // 주소유사 기반 관련성 임계
    private static final double CORE_NAME_SIM_MIN = 0.70; // 코어 이름 유사도 임계
    private static final double CORE_TOK_MIN      = 0.50; // 코어 토큰 겹침 임계

    private static final String[] AREA_POI_KEYWORDS = {
            "휴양림","공원","해수욕장","계곡","리조트","스키장","테마파크","유원지","캠핑장","국립공원"
    };
    private static final String[] FACILITY_SUFFIX = {
            "전기차충전소","주차장","매표소","관리사무소","안내소","정문","후문","입구","매점"
    };

    /**
     * 한 번에 결론을 내는 매칭 엔트리포인트.
     * MATCHED는 Issue를 남기지 않고, NEED_REVIEW/CONFLICT/NOT_FOUND만 Issue 저장.
     * NOT_FOUND는 즉시 is_active=false 처리.
     */
    @Transactional
    public MatchDecision tryMatch(Place place) {
        final String nameN = U.normalizeName(place.getName());
        final String nameC = U.removeFacilityNoise(nameN);
        final String addrN = U.normalizeAddress(place.getAddress());
        final String phoneN = place.getTel();

        //1. 앵커 좌표 확보: (mapX,mapY) + 주소 지오코딩 결과(최상위 1건)
        List<Coord> anchors = resloveAnchors(place, addrN);

        // 2) 후보 수집: 주소 + 키워드(원형+토큰), dedup
        Retrieval ret = collectCandidates(addrN, nameN);

        // 앵커가 있으면 3km 이내로 전역 노이즈 컷
        List<Candidate> candidates = pruneByAnchors(anchors, ret.all());

        //후보가 없을 경우, 조기 종료(NOT_FOUND)
        // 앵커 있음 + 후보 전무 + 주소검색도 전무 → 강한 NOT_FOUND
        if (!anchors.isEmpty() && candidates.isEmpty() && ret.addrDocCount() == 0) {
            // 앵커 있음 + 후보 전무 + 주소검색도 전무 → 강한 NOT_FOUND
//            deactivate(place);
            saveIssueEmpty(place, MatchStatus.NOT_FOUND);
            return new MatchDecision(
                    MatchStatus.NOT_FOUND, 0.0,
                    null, null, null, null, null,
                    0, 0, 0, Double.NaN, 0,
                    false, false
            );
        }
        if (anchors.isEmpty() && candidates.isEmpty()) {
            // 위치(장소위치도 없음) 기준 자체가 없어서 폐업 단정 금지 → CONFLICT
            saveIssueEmpty(place, MatchStatus.CONFLICT);
            return new MatchDecision(
                    MatchStatus.CONFLICT, 0.0,
                    null, null, null, null, null,
                    0, 0, 0, Double.NaN, 0,
                    false, false
            );
        }
        if (candidates.isEmpty()) {
            // 후보가 0인데 주소검색은 있었다 → 애매. 없음 단정 X
            saveIssueEmpty(place, MatchStatus.CONFLICT);
            return new MatchDecision(
                    MatchStatus.CONFLICT, 0.0,
                    null, null, null, null, null,
                    0, 0, 0, Double.NaN, 0,
                    false, false
            );
        }

        // 4) 스코어링 + 최적 후보 선택
        Scored scored = pickBest(place, candidates, anchors, nameN, nameC, addrN);

        //=== 장소 상태 판정===
        // A) 존재 확정(주소/이름 완전 일치)
        boolean existsStrong = (scored.distMeters() <= 100) &&
                (scored.nameSim() >= 0.82 || scored.tokenOverlap() >= 0.60);
        boolean existsAddrStrong = (scored.distMeters() <= 100) &&
                (scored.addrSim() >= 0.50);
        boolean existsByTokenExact = scored.tokenOverlap() == 1.0;

        if (existsStrong || existsAddrStrong || existsByTokenExact) {
            log.debug("EXISTS placeId={} dist={}m nameSim={} tok={} addrSim={}",
                    place.getId(), fmt(scored.distMeters()), fmt(scored.nameSim()),
                    fmt(scored.tokenOverlap()), fmt(scored.addrSim()));
            return toDecision(MatchStatus.MATCHED, scored, false, false);
        }

        //이름이 강하게 맞으면(0.80이상) 거리 허용(1000m까지 완화)
        boolean existsByNameStrong =
                (scored.coreNameSim() >= 0.80 && scored.distMeters() <= 3000) ||
                        (scored.coreTok()  >= 0.70      && scored.distMeters() <= 300);

        if (existsByNameStrong) {
            log.debug("EXISTS(NAME-STRONG) id={} dist={} coreNameSim={} coreTok={}",
                    place.getId(), fmt(scored.distMeters()), fmt(scored.coreNameSim()), fmt(scored.coreTok()));
            return toDecision(MatchStatus.MATCHED, scored, false, false);
        }

        // ★ 영역형 POI 전용 존재 규칙 추가
        if (isAreaPoi(place) && scored.best() != null) {
            String coreBest = stripFacilitySuffix(scored.best().name());
            double coreNameSim = U.nameSim(U.normalizeName(place.getName()), coreBest);
            double coreTok     = U.tokenOverlap(U.normalizeName(place.getName()), coreBest);

            boolean existsArea = (scored.distMeters() <= 3000) &&
                    (scored.addrSim() >= 0.75 || coreTok >= 0.50 || coreNameSim >= 0.70);

            if (existsArea) {
                log.debug("EXISTS(AREA) placeId={} dist={}m addrSim={} coreNameSim={} coreTok={}",
                        place.getId(), fmt(scored.distMeters()), fmt(scored.addrSim()),
                        fmt(coreNameSim), fmt(coreTok));
                return toDecision(MatchStatus.MATCHED, scored, false, false);
            }
        }

        // B) 리네임 의심
        boolean renameSuspect = (scored.distMeters() <= SAME_PLACE_DIST_STRONG) &&
                (scored.addrSim() >= ADDR_SIM_WEAK) &&
                (scored.nameSim() < RENAME_NAME_SIM_MAX);
        if (renameSuspect) {
            saveIssueWithScores(place, MatchStatus.NEED_REVIEW, scored.best(),
                    scored.nameSim(), scored.tokenOverlap(), scored.addrSim(),
                    scored.distMeters(), scored.distScore(), scored.finalScore(),
                    true, false);

            return toDecision(MatchStatus.MATCHED, scored, true, false);//폐업 필터링이 목적이므로, 추후 status 변경 필요
        }

        // C) 이전 의심(근거리 이동)
        boolean movedSuspect = (scored.nameSim() >= NAME_SIM_EXISTS) &&
                (scored.distMeters() > MOVED_DIST_MIN && scored.distMeters() <= MOVED_DIST_MAX);
        if (movedSuspect) {
            saveIssueWithScores(place, MatchStatus.NEED_REVIEW, scored.best(),
                    scored.nameSim(), scored.tokenOverlap(), scored.addrSim(),
                    scored.distMeters(), scored.distScore(), scored.finalScore(),
                    false, true);
            return toDecision(MatchStatus.MATCHED, scored, false, true); //폐업 필터링이 목적이므로, 추후 status 변경 필요
        }

        // D) 없음(폐업/소멸) 확정: 앵커 있고, 반경 1.5km 내 '관련성' 후보가 0
        if (!anchors.isEmpty()) {
            int radius = isAreaPoi(place) ? AREA_RELATED_RADIUS : SCAN_RADIUS_MAX;

            boolean relatedWithinRadius = candidates.stream().anyMatch(c -> {
                double m = minDistance(anchors, c.lon(), c.lat());
                if (Double.isNaN(m) || m > radius) return false;
                // 관련성: 이름/토큰 어느 하나라도 중간 이상이거나, 전화 일치
                // 코어 이름 비교(부속시설 접미사 제거)
                String cn     = U.normalizeName(c.name());
                String cnCore = U.removeFacilityNoise(cn);

                double n  = U.nameSim(U.normalizeName(place.getName()), cn);
                double nc = U.nameSim(U.removeFacilityNoise(U.normalizeName(place.getName())), cnCore);
                double t  = U.tokenOverlap(U.normalizeName(place.getName()), cn);
                double tc = U.tokenOverlap(U.removeFacilityNoise(U.normalizeName(place.getName())), cnCore);
                double a  = U.tokenOverlap(U.normalizeAddress(place.getAddress()), U.normalizeAddress(c.address()));

                // 기존 이름/토큰 + 주소 유사도 보강
                return Math.max(n,nc) >= 0.60 || Math.max(t,tc) >= 0.50 || a >= ADDR_SIM_RELATED;
            });
            if (!relatedWithinRadius) {
//                deactivate(place);
                saveIssueWithScores(place, MatchStatus.NOT_FOUND, scored.best(),
                        scored.nameSim(), scored.tokenOverlap(), scored.addrSim(),
                        scored.distMeters(), scored.distScore(), scored.finalScore(),
                        false, false);
                return toDecision(MatchStatus.NOT_FOUND, scored, false, false);
            }
        }

        // E) 애매 → CONFLICT
        saveIssueWithScores(place, MatchStatus.CONFLICT, scored.best(),
                scored.nameSim(), scored.tokenOverlap(), scored.addrSim(),
                scored.distMeters(), scored.distScore(), scored.finalScore(),
                false, false);
        return toDecision(MatchStatus.CONFLICT, scored, false, false);
    }

    /**
     *
     * @param place 장소
     * @param addrN 주소
     * @return 장소 좌표 + 주소 기반 검색 카카오 장소 좌표(최상위 1건)
     */
    private List<Coord> resloveAnchors(Place place, String addrN) {
        List<Coord> anchors = new ArrayList<>();
        if(place.getMapX() != null && place.getMapY() !=null) {
            anchors.add(new Coord(place.getMapX(), place.getMapY()));
        }
        if(addrN != null && !addrN.isBlank()) {
            try {
                KakaoAddress ka = kakao.getPlaceInfoByAddress(addrN,null);
                if (ka != null && ka.documents() != null && !ka.documents().isEmpty()) {
                    var d = ka.documents().get(0);
                    Double lon = parseDouble(d.x());
                    Double lat = parseDouble(d.y());
                    if (lon != null && lat != null) anchors.add(new Coord(lon, lat));
                }
            } catch (Exception e) {
                log.debug("address geocode failed: {}", e.getMessage());
            }
        }
        return anchors;
    }

    /**
     *
     * @param addrN 주소
     * @param nameN 장소명
     * @return 주소 기반 카카오 장소 정보 + 장소명으로 키워드 추출하여 키워드 기반 카카오 장소 정보
     */
    private Retrieval collectCandidates(String addrN, String nameN) {
        List<Candidate> out = new ArrayList<>();
        int addrDocs = 0;

        //주소 검색
        if(addrN !=null && !addrN.isBlank()) {
            try{
                KakaoAddress ka = kakao.getPlaceInfoByAddress(addrN,null);
                if (ka != null && ka.documents() != null) {
                    addrDocs = ka.documents().size();
                    for(var d : ka.documents()) {
                        Double lon = parseDouble(d.x());
                        Double lat = parseDouble(d.y());
                        String a = (d.road_address()!=null && d.road_address().address_name()!=null)
                                ? d.road_address().address_name()
                                : (d.address()!=null ? d.address().address_name() : null);
                        out.add(new Candidate(
                                "ADDR", "addr:"+addrN,
                                null, a, lon, lat,
                                null, null
                        ));
                    }
                }
            } catch (Exception e) {
                log.debug("kakao address search error: {}", e.getMessage());
            }
        }

        //장소명으로 키워드 추출하여 키워드 기반 카카오 장소 정보 가져오기
        for (String kw : U.expandKeywords(nameN)) {
            try {
                KakaoKeyword kk = kakao.getPlaceInfoByKeyword(kw,null);
                if (kk != null && kk.documents() != null) {
                    for (var d : kk.documents()) {
                        Double lon = parseDouble(d.x());
                        Double lat = parseDouble(d.y());
                        String addr = (d.road_address_name()!=null && !d.road_address_name().isBlank())
                                ? d.road_address_name() : d.address_name();
                        out.add(new Candidate(
                                "KW", d.id(),
                                d.place_name(), addr, lon, lat,
                                d.phone(), d.place_url()
                        ));
                    }
                }
            } catch (Exception e) {
                log.debug("kakao keyword search error ({}): {}", kw, e.getMessage());
            }
            if (out.size() > 300) break; // 후보 과다 방지
        }

        // dedup: (address|lon|lat) 키 기준
        List<Candidate> dedup = out.stream().collect(Collectors.collectingAndThen(
                Collectors.toMap(
                        c -> (nullToEmpty(c.address()) + "|" + c.lon() + "|" + c.lat()),
                        c -> c, (a,b)->a, LinkedHashMap::new
                ),
                m -> new ArrayList<>(m.values())
        ));
        return new Retrieval(dedup, addrDocs);
    }

    //앵커 기준 반경 필터링(기본 3km)
    private List<Candidate> pruneByAnchors(List<Coord> anchors, List<Candidate> all) {
        if(anchors.isEmpty()) return all;
        return all.stream().filter(c -> {
            double m = minDistance(anchors, c.lon(), c.lat());
            return !Double.isNaN(m) && m<= PlaceMatchingService.GLOBAL_FILTER_RADIUS;
        }).collect(Collectors.toList());
    }

    //스코어링 및 최적의 후보 찾기
    private Scored pickBest(Place p, List<Candidate> cands, List<Coord> anchors,
                            String nameNorm, String nameCore, String addrNorm) {
        Candidate best = null;
        double bestScore=-1, nameBest=0, tokBest=0, addrBest=0, metersBest=Double.NaN, distBest=0;
        double coreNameBest=0, coreTokBest=0;

        for (Candidate c : cands) {
            String cn     = U.normalizeName(c.name());         // 후보 원본 정규화
            String cnCore = U.removeFacilityNoise(cn);         // 후보 코어네임
            String ca     = U.normalizeAddress(c.address());

            // 원본/코어 각각 유사도 → 더 큰 값 채택
            double nameRaw = U.nameSim(nameNorm, cn);
            double nameCor = U.nameSim(nameCore, cnCore);
            double name    = Math.max(nameRaw, nameCor);

            double tokRaw  = U.tokenOverlap(nameNorm, cn);
            double tokCor  = U.tokenOverlap(nameCore, cnCore);
            double tok     = Math.max(tokRaw, tokCor);

            double adr     = U.tokenOverlap(addrNorm, ca);

            double meters  = anchors.isEmpty()
                    ? U.distanceMeters(p.getMapX(), p.getMapY(), c.lon(), c.lat())
                    : minDistance(anchors, c.lon(), c.lat());
            double dist    = U.distScore(meters);

            double score = 0.52*name + 0.18*tok + 0.12*adr + 0.18*dist;

            if (score > bestScore) {
                best = c; bestScore = score;
                nameBest = name; tokBest = tok; addrBest = adr;
                metersBest = meters; distBest = dist;
                coreNameBest = nameCor; coreTokBest = tokCor;
            }
        }

        // 플래그(리네임/이전) 계산
        //이름만 바뀐 같은 장소 의심
        boolean renameSuspect = (!Double.isNaN(metersBest) && metersBest <= SAME_PLACE_DIST_STRONG)
                && (addrBest >= ADDR_SIM_WEAK) && (nameBest < RENAME_NAME_SIM_MAX);
        //근거리 이전 의심
        boolean movedSuspect = (nameBest >= NAME_SIM_EXISTS)
                && (metersBest > MOVED_DIST_MIN && metersBest <= MOVED_DIST_MAX);

        return new Scored(best, nameBest, tokBest, addrBest, metersBest, distBest, bestScore,
                renameSuspect, movedSuspect, coreNameBest, coreTokBest);
    }

    // === 이슈 저장(점수 포함) ===
    private void saveIssueWithScores(
            Place place, MatchStatus status, Candidate best,
            double nameSim, double tokenOverlap, double addrSim,
            double distMeters, double distScore, double finalScore,
            boolean renameSuspect, boolean movedSuspect
    ) {
        PlaceMatchIssue issue = PlaceMatchIssue.builder()
                .placeId(place.getId())
                .placeAddress(place.getAddress())
                .placeName(place.getName())
                .kakaoAddress(best != null ? best.address() : null)
                .kakaoPlaceName(best != null ? best.name() : null)
                .nameSim(dNa(nameSim))
                .tokenOverlap(dNa(tokenOverlap))
                .addrSim(dNa(addrSim))
                .distMeters(dNaOrNull(distMeters))
                .distScore(dNa(distScore))
                .finalScore(dNa(finalScore))
                .matchStatus(status)
                .renameSuspect(renameSuspect)
                .movedSuspect(movedSuspect)
                .build();
        issueRepo.save(issue);
    }

    // === 후보/점수 자체가 없을 때(강한 NOT_FOUND, 후보 0개 CONFLICT 등) ===
    private void saveIssueEmpty(Place place, MatchStatus status) {
        PlaceMatchIssue issue = PlaceMatchIssue.builder()
                .placeId(place.getId())
                .placeAddress(place.getAddress())
                .placeName(place.getName())
                .kakaoAddress(null)
                .kakaoPlaceName(null)
                .nameSim(null).tokenOverlap(null).addrSim(null)
                .distMeters(null).distScore(null).finalScore(null)
                .matchStatus(status)
                .renameSuspect(false)
                .movedSuspect(false)
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
                rename, moved
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
    private boolean isAreaPoi(Place p) {
        String n = p.getName()==null? "" : p.getName();
        for (String k : AREA_POI_KEYWORDS) if (n.contains(k)) return true;
        String cat = p.getCategory()==null? "" : p.getCategory();
        for (String k : AREA_POI_KEYWORDS) if (cat.contains(k)) return true;
        return false;
    }
    private String stripFacilitySuffix(String name) {
        if (name == null) return null;
        String s = U.normalizeName(name); // 기존 정규화 활용
        for (String suf : FACILITY_SUFFIX) {
            s = s.replace(suf, "");
        }
        return s.trim();
    }
    private static String nullToEmpty(String s) {
        return s==null?"":s;
    }
    private static String fmt(Double d) {
        return d==null?"-":String.format("%.1f",d);
    }
    private static Double dNa(double v)       { return Double.isNaN(v) ? null : v; }
    private static Double dNaOrNull(double v) { return Double.isNaN(v) ? null : v; }
    //===내부 record===
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
            double coreNameSim,
            double coreTok
    ) {}
}
