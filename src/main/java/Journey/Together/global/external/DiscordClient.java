package Journey.Together.global.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import Journey.Together.global.config.DiscordFeignConfiguration;
import Journey.Together.global.external.dto.DiscordMessage;

@FeignClient(
	name = "discord-client",
	url = "https://discord.com/api/webhooks/1380907427234582722/ZYXX60PqalyHfCxxC5Vh38lF2IInXw_l1lQjEoNgQUR_IXoM8bHIKMwohUi6dZLZRAh4",
	configuration = DiscordFeignConfiguration.class)
public interface DiscordClient {

	@PostMapping()
	void sendAlarm(@RequestBody DiscordMessage message);
}
