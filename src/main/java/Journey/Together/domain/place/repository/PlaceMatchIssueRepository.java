package Journey.Together.domain.place.repository;

import Journey.Together.domain.place.entity.PlaceMatchIssue;
import Journey.Together.domain.place.enumerated.MatchStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PlaceMatchIssueRepository extends JpaRepository<PlaceMatchIssue, Long> {
    long countByMatchStatusAndMatchedAtBetween(MatchStatus status, LocalDateTime from, LocalDateTime to);

    List<PlaceMatchIssue> findByMatchStatusAndMatchedAtBetweenOrderByMatchedAtDesc(
            MatchStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable
    );
}
