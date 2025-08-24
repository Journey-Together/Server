package Journey.Together.global.common.discord;

import Journey.Together.domain.place.entity.PlaceMatchIssue;
import Journey.Together.domain.place.enumerated.MatchStatus;
import Journey.Together.global.external.MatchNotifitionDiscordClient;
import Journey.Together.global.external.dto.DiscordMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class DiscordMatchNotifitionSender {
    private final MatchNotifitionDiscordClient discordClient;
    private final Environment environment;

    // 디스코드 임베드 description 최대 4096자. 여유를 두고 3800자로 절단
    private static final int EMBED_DESC_LIMIT = 3800;
    // 상태별 최대 행수(임베드 길이 보호용)
    private static final int MAX_ROWS_PER_STATUS = 30;

    /** 일반 단건 알림 (기존 메서드) */
    public void sendDiscordAlarm(String content, String title, String description) {
        if (!isReleaseProfile()) return;

        try {
            discordClient.sendAlarm(createMessage(content, title, description));
        } catch (Exception ex) {
            log.error("❌ Discord 알림 전송 실패: {}", ex.getMessage());
        }
    }

    /** 매칭 요약 알림: 상태별 개수 + 간단 목록(필드 제한) */
    public void sendMatchSummary(String titile,
                                 long needReviewCount, List<PlaceMatchIssue> needReviewItems,
                                 long conflictCount,   List<PlaceMatchIssue> conflictItems,
                                 long notFoundCount,   List<PlaceMatchIssue> notFoundItems) {
        String content = "# (" + titile + ")\n"
                + "- NEED_REVIEW: " + needReviewCount +"\n"
                + "- CONFLICT: " + conflictCount+"\n"
                + "- NOT_FOUND: " + notFoundCount;

        List<DiscordMessage.Embed> embeds = new ArrayList<>(3);
        embeds.add(buildStatusEmbed(MatchStatus.NEED_REVIEW, needReviewCount, needReviewItems));
        embeds.add(buildStatusEmbed(MatchStatus.CONFLICT,   conflictCount,   conflictItems));
        embeds.add(buildStatusEmbed(MatchStatus.NOT_FOUND,  notFoundCount,  notFoundItems));

        DiscordMessage message = DiscordMessage.builder()
                .content(content)
                .embeds(embeds)
                .build();

        try {
            discordClient.sendAlarm(message);
        } catch (Exception ex) {
            log.error("❌ Discord 요약 전송 실패: {}", ex.getMessage());
        }
    }

    /** 상태별 임베드 구성: status, placeId/name/address, kakaoPlaceName/address 만 표시 */
    private DiscordMessage.Embed buildStatusEmbed(MatchStatus status, long count, List<PlaceMatchIssue> rows) {
        String title = switch (status) {
            case NEED_REVIEW -> "🟨 NEED_REVIEW (" + count + ")";
            case CONFLICT   -> "🟧 CONFLICT (" + count + ")";
            case NOT_FOUND  -> "🟥 NOT_FOUND (" + count + ")";
            default         -> status.name() + " (" + count + ")";
        };

        // 최신순 상위 N개만, 그리고 임베드 길이도 제한
        List<PlaceMatchIssue> list = Optional.ofNullable(rows).orElseGet(Collections::emptyList)
                .stream()
                .limit(MAX_ROWS_PER_STATUS)
                .toList();

        StringBuilder sb = new StringBuilder();
        for (PlaceMatchIssue r : list) {
            // 한 항목을 3줄로: 헤더(상태/ID/이름), 공공주소, 카카오명/주소
            sb.append("• [").append(safeStatus(r)).append("] ")
                    .append(safeId(r)).append(" — ").append(safe(r.getPlaceName())).append("\n")
                    .append("   ").append(safe(r.getPlaceAddress())).append("\n")
                    .append("   ↳ ").append(safe(r.getKakaoPlaceName()))
                    .append(" / ").append(safe(r.getKakaoAddress())).append("\n\n");

            if (sb.length() >= EMBED_DESC_LIMIT) {
                sb.append("… (truncated)\n");
                break;
            }
        }

        String description =
                "### 🕖 발생 시간\n" + LocalDateTime.now() + "\n\n" +
                        (sb.isEmpty() ? "_no items in this window_" : sb.toString());

        return DiscordMessage.Embed.builder()
                .title(title)
                .description(description)
                .build();
    }

    private boolean isReleaseProfile() {
        try {
            return Arrays.stream(environment.getActiveProfiles())
                    .anyMatch(p -> p != null && p.contains("release"));
        } catch (Exception e) {
            return false;
        }
    }

    private DiscordMessage createMessage(String content, String title, String description) {
        return DiscordMessage.builder()
                .content("# " + content)
                .embeds(List.of(
                        DiscordMessage.Embed.builder()
                                .title(title)
                                .description("### 🕖 발생 시간\n" + LocalDateTime.now() + "\n\n" + "### " + description + "\n")
                                .build()
                ))
                .build();
    }

    // === helpers ===
    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
    private static String safeStatus(PlaceMatchIssue r) {
        return r.getMatchStatus() == null ? "-" : r.getMatchStatus().name();
    }
    private static String safeId(PlaceMatchIssue r) {
        return r.getPlaceId() == null ? "-" : String.valueOf(r.getPlaceId());
    }
}
