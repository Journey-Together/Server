package Journey.Together.global.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.external.DiscordClient;
import Journey.Together.global.external.dto.DiscordMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DiscordErrorSender {
	private final DiscordClient discordClient;

	public void sendDiscordAlarm(Exception e, WebRequest request) {
		try {
			discordClient.sendAlarm(createMessage(e, request));
		} catch (Exception ex) {
			log.error("❌ Discord 알림 전송 실패: {}", ex.getMessage());
		}
	}

	private DiscordMessage createMessage(Exception e, WebRequest request) {
		DiscordMessage message;

		if (e instanceof ApplicationException appEx) {
			message = DiscordMessage.builder()
				.content("# 🚨 ApplicationException 발생")
				.embeds(List.of(
					DiscordMessage.Embed.builder()
						.title("❗️ 에러 정보")
						.description(
							"### 🕖 발생 시간\n"
								+ LocalDateTime.now()
								+ "\n### 🔗 요청 URL\n"
								+ createRequestFullPath(request)
								+ "\n### 📄 ErrorCode\n"
								+  "[" + appEx.getErrorCode().getCode() + "] "
								+ appEx.getErrorCode().getMessage()
								+ "\n### 📄 StackTrace\n"
								+ "```\n"
								+ getStackTrace(e).substring(0, 1000)
								+ "\n```")
						.build()
				))
				.build();
		} else {
			// 일반적인 예외는 메시지와 스택트레이스만 전송
			message = DiscordMessage.builder()
				.content("# 🚨 예외 발생")
				.embeds(List.of(
					DiscordMessage.Embed.builder()
						.title("❗️ 에러 정보")
						.description(
							"### 🕖 발생 시간\n"
								+ LocalDateTime.now()
								+ "\n### 🔗 요청 URL\n"
								+ createRequestFullPath(request)
								+ "\n### 📄 메시지\n"
								+ e.getMessage()
								+ "\n### 📄 StackTrace\n"
								+ "```\n"
								+ getStackTrace(e).substring(0, 1000)
								+ "\n```")
						.build()
				))
				.build();
		}

		return message;
	}

	private String createRequestFullPath(WebRequest webRequest) {
		HttpServletRequest request = ((ServletWebRequest)webRequest).getRequest();
		String fullPath = request.getMethod() + " " + request.getRequestURL();

		String queryString = request.getQueryString();
		if (queryString != null) {
			fullPath += "?" + queryString;
		}

		return fullPath;
	}

	private String getStackTrace(Exception e) {
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}
}
