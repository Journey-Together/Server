package Journey.Together.global.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import Journey.Together.global.config.OpenFeignConfig;
import Journey.Together.global.external.dto.DiscordMessage;

@FeignClient(
	name = "message-discord-client",
	url = "${discord.message-webhook-url}",
	configuration = OpenFeignConfig.class)
public interface MessageDiscordClient {

	@PostMapping()
	void sendAlarm(@RequestBody DiscordMessage message);
}