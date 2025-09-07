package Journey.Together.domain.place.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.RedisCallback;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Journey.Together.domain.place.dto.Suggestion;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceAutoCompleteService {

	private final StringRedisTemplate redis;
	private final PlaceRepository placeRepository;

	private static final Integer LIMIT = 5;
	private static final Integer OVER_SAMPLE = 5;
	private static final String LEX_KEY   = "autocomplete:lex";
	private static final String SCORE_KEY = "autocomplete:score";

	public void syncPlaceNamesWithRedis(int pageSize) {
		int page = 0;
		while (true) {
			Page<String> names = placeRepository.findAll(PageRequest.of(page, pageSize)).map(Place::getName);
			if (names.isEmpty()) break;

			List<String> batch = names.getContent().stream()
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.distinct()
				.toList();

			if (!batch.isEmpty()) {
				redis.executePipelined((RedisCallback<Object>) conn -> {
					for (String w : batch) {
						conn.zAdd(LEX_KEY.getBytes(StandardCharsets.UTF_8), 0.0, w.getBytes(StandardCharsets.UTF_8)); // 사전순
						conn.zAdd(SCORE_KEY.getBytes(StandardCharsets.UTF_8), 0.0, w.getBytes(StandardCharsets.UTF_8)); // 인기순
					}
					return null;
				});
			}
			if (!names.hasNext()) break;
			page++;
		}
	}

	public List<Suggestion> suggest(String prefix) {
		if (prefix.isBlank() || LIMIT <= 0) return List.of();

		// [prefix, prefix + \u00ff] 범위 (접두어 매칭을 위한 상한 트릭)
		Range range = Range.range().gte(prefix).lte(prefix + "\u00ff");

		// oversample: lex로 넉넉히 가져와서 인기점수로 재정렬
		int take = Math.max(LIMIT * Math.max(OVER_SAMPLE, 1), LIMIT);

		Set<String> cand = redis.opsForZSet().rangeByLex(LEX_KEY, range.toRange(), Limit.limit().count(take));

		if (cand == null || cand.isEmpty()) return List.of();

		// 점수 일괄 조회 파이프라인
		List<String> list = new ArrayList<>(cand); // 사전순 유지
		List<Object> scores = redis.executePipelined((RedisCallback<Object>) c -> {
			for (String w : list) {
				c.zScore(SCORE_KEY.getBytes(StandardCharsets.UTF_8),
					w.getBytes(StandardCharsets.UTF_8));
			}
			return null;
		});

		// (단어, 점수, 장소id) 구성
		List<Suggestion> paired = new ArrayList<>(list.size());
		for (int i = 0; i < list.size(); i++) {
			String w = list.get(i);
			Object s = scores.get(i);
			double score = (s instanceof Double d) ? d : 0.0;
			Long placeId = placeRepository.findPlaceByName(w)
				.map(Place::getId)
				.orElse(null);
			paired.add(new Suggestion(w, score, placeId));
		}

		// 인기 점수 내림차순 → 동점 시 사전순
		paired.sort(Comparator.<Suggestion>comparingDouble(Suggestion::score).reversed()
			.thenComparing(Suggestion::word));

		return paired.size() > LIMIT ? paired.subList(0, LIMIT) : paired;
	}

	/** 사용자가 항목을 선택했을 때 인기 점수 증가 */
	public double recordSelection(String word, double inc) {
		if (word == null || word.isBlank()) return 0.0;
		Double v = redis.opsForZSet().incrementScore(SCORE_KEY, word, inc);
		return v == null ? 0.0 : v;
	}
}