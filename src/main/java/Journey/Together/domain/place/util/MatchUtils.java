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
        if(raw == null) return null;
        String s = PAREN.matcher(raw).replaceAll("");
        s = s.replaceAll("[^ㄱ-힣0-9a-zA-Z\\s]", "");
        for (String n : NAME_NOISE) s = s.replace(n, "");
        return s.trim();
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
