package Journey.Together.global.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import Journey.Together.global.config.OpenFeignConfig;
import Journey.Together.global.external.dto.DiscordMessage;

@FeignClient(
	name = "discord-client",
	url = "${discord.webhook-url}",
	configuration = OpenFeignConfig.class)
public interface DiscordClient {

	@PostMapping()
	void sendAlarm(@RequestBody DiscordMessage message);
}