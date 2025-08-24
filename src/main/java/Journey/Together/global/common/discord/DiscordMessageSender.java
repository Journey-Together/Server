package Journey.Together.global.common.discord;

import Journey.Together.global.external.MessageDiscordClient;
import Journey.Together.global.external.dto.DiscordMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DiscordMessageSender {
	private final MessageDiscordClient discordClient;
	private final Environment environment;

	public void sendDiscordAlarm(String content, String title, String description) {
		if (Arrays.asList(environment.getActiveProfiles()).get(0).contains("release")) {
			try {
				discordClient.sendAlarm(createMessage(content, title, description));
			} catch (Exception ex) {
				log.error("❌ Discord 알림 전송 실패: {}", ex.getMessage());
			}
		}

	}

	private DiscordMessage createMessage(String content, String title, String description) {
		DiscordMessage message;

		message = DiscordMessage.builder()
			.content("# " + content)
			.embeds(List.of(
				DiscordMessage.Embed.builder()
					.title(title)
					.description(
						"### 🕖 발생 시간\n"
							+ LocalDateTime.now()
							+ "\n"
							+ "\n"
							+ "### " + description
							+ "\n")
					.build()
			)).build();

		return message;
	}
}
