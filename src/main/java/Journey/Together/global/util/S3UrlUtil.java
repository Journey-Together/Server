package Journey.Together.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3UrlUtil {

    private final S3Client s3Client;

    public String generateProfileUrl(String profileUuid) {
        return s3Client.baseUrl() + profileUuid + "/profile_" + profileUuid;
    }
}