package Journey.Together.domain.place.service.query;

import Journey.Together.domain.place.entity.Place;
import Journey.Together.domain.place.repository.PlaceRepository;
import Journey.Together.domain.plan.dto.PlaceInfo;
import Journey.Together.domain.plan.dto.PlaceInfoPageRes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceQueryService {

    private final PlaceRepository placeRepository;

    @Transactional(readOnly = true)
    public PlaceInfoPageRes searchPlace(String word, Pageable page) {
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("createdAt").descending());
        Page<Place> placePage = placeRepository.findAllByNameContainsOrderByCreatedAtDesc(word, pageable);
        List<PlaceInfo> placeInfoList = placePage.getContent().stream()
                .map(PlaceInfo::of)
                .collect(Collectors.toList());

        return PlaceInfoPageRes.of(
                placeInfoList,
                placePage.getNumber(),
                placePage.getSize(),
                placePage.getTotalPages(),
                placePage.isLast(),
                placePage.getTotalElements()
        );
    }
}

