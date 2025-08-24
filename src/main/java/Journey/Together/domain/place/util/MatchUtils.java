package Journey.Together.domain.place.util;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class MatchUtils {
    private static final Pattern PAREN = Pattern.compile("[\\(\\[\\{].*?[\\)\\]\\}]");
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");
    //한글 일치율 비교 알고리즘
    private static final JaroWinklerDistance JW = new JaroWinklerDistance();
    //자주 쓰이는 수식어
    private static final Set<String> NAME_NOISE = Set.of("지점","유스호스텔","리조트","관광지","주식회사","점");
    private static final double EARTH_RADIUS_M = 6_371_008.8;

    //장소명 정규화- 괄호 및 특수문자 제거, 자주 쓰이는 수식어 제거
    public String normalizeName(String raw) {
        if (raw == null) return "";
        String s = java.text.Normalizer.normalize(raw, java.text.Normalizer.Form.NFKC);

        // 1) 모든 괄호류/마크다운 표기 제거: (), [], {}, <> , 《》, 〈〉, 「」, 『』, 【】, ［］, [[...]]
        s = stripBrackets(s);

        // 2) 소문자화 + 특수기호 → 공백
        s = s.toLowerCase(Locale.KOREAN).replaceAll("[^가-힣0-9a-z]+", " ");

        // 3) 행정구 접두어 제거(이름 맨 앞에 있을 때만)
        s = removeLeadingAdminPrefix(s);

        // 4) 시설 접미사/노이즈 토큰 제거 (주차장/전기차충전소/관리사무소/안내소/정문/후문/입구 등)
        s = removeFacilityNoise(s);

        // 5) 다중 공백 정리 + 완전 합침(문자단위 유사도용)
        s = s.replaceAll("\\s+", "");
        return s.trim();
    }

    private String stripBrackets(String s) {
        // markdown [[...]]
        s = s.replaceAll("\\[\\[[^\\]]*\\]\\]", " ");
        // 일반 [], (), {}, <>
        s = s.replaceAll("\\([^)]*\\)", " ")
                .replaceAll("\\[[^\\]]*\\]", " ")
                .replaceAll("\\{[^}]*\\}", " ")
                .replaceAll("\\<[^>]*\\>", " ");
        // 전각/한글권 따옴괄호류
        s = s.replaceAll("《[^》]*》", " ")
                .replaceAll("〈[^〉]*〉", " ")
                .replaceAll("「[^」]*」", " ")
                .replaceAll("『[^』]*』", " ")
                .replaceAll("【[^】]*】", " ")
                .replaceAll("［[^］]*］", " ");
        return s;
    }

    private static final Set<String> ADMIN_PREFIX = Set.of(
            "서울","서울특별시","경기","경기도","인천","부산","대구","광주","대전","울산","세종",
            "강원","강원특별자치도","충북","충청북도","충남","충청남도","전북","전북특별자치도",
            "전남","전라남도","경북","경상북도","경남","경상남도","제주","제주특별자치도","청주","포천"
    );
    private String removeLeadingAdminPrefix(String s) {
        // 맨 앞 토큰이 행정구면 삭제
        var m = Pattern.compile("^(" + String.join("|", ADMIN_PREFIX) + ")\\s+").matcher(s);
        return m.find() ? s.substring(m.end()) : s;
    }

    private static final Set<String> FACILITY_NOISE = Set.of(
            "주차장","전기차충전소","관리사무소","안내소","정문","후문","입구","출구","매표소","매표","전망대",
            "별관","본점","지점","점","본관","센터","홀","관","동","관람안내","게이트","터미널","사무소","캠핑장","팬션","축제","남문","광장",
            "매점"
    );
    public String removeFacilityNoise(String s) {
        // 토큰 단위로 노이즈 삭제 후 다시 붙임
        String[] toks = s.split("\\s+");
        StringBuilder b = new StringBuilder();
        for (String t : toks) {
            if (t.isBlank()) continue;
            if (FACILITY_NOISE.contains(t)) continue;
            b.append(t).append(' ');
        }
        return b.toString().trim();
    }

    public String normalizeAddress(String raw) {
        if (raw == null) return null;
        return raw.replaceAll("\\s+", " ").trim();
    }

    //장소명 비슷한 정도 계산, 비교값이 없거나 null이면 0.0
    public double nameSim(String a, String b) {
        if (a == null || b == null || a.isBlank() || b.isBlank()) return 0.0;
        return JW.apply(a, b);
    }

    //두 문자열을 공백으로 나눈 단어 집합 사이의 자카드 유사도 계산
    //1.0에 가까울수록 비슷한 단어
    public double tokenOverlap(String a, String b) {
        if(a==null || b==null) return 0.0;
        Set<String> sa = new HashSet<>(Arrays.asList(a.split(" ")));
        Set<String> sb = new HashSet<>(Arrays.asList(b.split(" ")));
        sa.removeIf(t -> t.length() < 2);
        sb.removeIf(t -> t.length() < 2);

        if (sa.isEmpty() || sb.isEmpty()) return 0.0;

        Set<String> inter = new HashSet<>(sa);
        inter.retainAll(sb);

        Set<String> uni = new HashSet<>(sa);
        uni.addAll(sb);

        //자카트 유사도 = 교집합/합집합
        return uni.isEmpty() ? 0.0 : (double) inter.size()/uni.size();
    }

    //두 장소의 경위도로 구면 거리 계산 (Haversine 공식)
    public double distanceMeters(Double lon1, Double lat1, Double lon2, Double lat2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double lat1r = Math.toRadians(lat1);
        double lat2r = Math.toRadians(lat2);

        double sinLat = Math.sin(dLat / 2);
        double sinLon = Math.sin(dLon / 2);

        double a = sinLat*sinLat + Math.cos(lat1r) * Math.cos(lat2r) * sinLon*sinLon;
        // 수치 안정성 보강
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(Math.max(0.0, 1.0 - a)));
        return EARTH_RADIUS_M * c;
    }

    /** 거리 점수: 0m=1.0, 멀수록 지수적으로 감소 */
    public double distScore(double meters) {
        if (Double.isNaN(meters)) return 0.0;
        return Math.max(0.0, Math.exp(-meters / 80.0));
    }

    /** 이름 기반 키워드: 원형 + 2글자 이상 토큰 */
    public List<String> expandKeywords(String normalizedName) {
        if (normalizedName == null || normalizedName.isBlank()) return List.of();
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add(normalizedName);
        Arrays.stream(normalizedName.split(" "))
                .filter(t -> t.length() >= 2)
                .forEach(set::add);
        return new ArrayList<>(set);
    }


}
