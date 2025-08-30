package Journey.Together.global.external;

import Journey.Together.global.config.OpenFeignConfig;
import Journey.Together.global.external.dto.DiscordMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "match-notifition-discord-client",
        url = "${discord.match-webhook-url}",
        configuration = OpenFeignConfig.class)
public interface MatchNotifitionDiscordClient {
    @PostMapping()
    void sendAlarm(@RequestBody DiscordMessage message);
}
