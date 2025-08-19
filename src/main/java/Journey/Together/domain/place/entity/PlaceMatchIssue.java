package Journey.Together.domain.place.entity;

import Journey.Together.domain.place.enumerated.MatchStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceMatchIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long placeId;
    private String placeAddress;
    private String kakaoAddress;
    private Double score;
    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;
    private Instant matchedAt;

    @Builder
    public PlaceMatchIssue (Place place, String kakaoAddress, Double score, MatchStatus matchStatus) {
        this.placeId = place.getId();
        this.placeAddress = place.getAddress();
        this.kakaoAddress = kakaoAddress;
        this.score = score;
        this.matchStatus = matchStatus;
        this.matchedAt = Instant.now();
    }
}
