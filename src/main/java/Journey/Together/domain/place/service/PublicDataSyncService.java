package Journey.Together.domain.place.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicDataSyncService {

	private final PublicDataService publicDataService;

	@Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
	public void runSyncJob() {
		log.info("공공데이터 스케줄링 동기화 시작");
		publicDataService.syncAllData();
	}
}