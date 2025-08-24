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
    private String placeName;
    private String kakaoAddress;
    private Double nameSim;             // Jaro-Winkler
    private Double tokenOverlap;        // Jaccard
    private Double addrSim;             // Jaccard
    private Double distMeters;          // 최단거리(m)
    private Double distScore;           // exp(-m/80)
    private Double finalScore;
    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;
    private Boolean renameSuspect;
    private Boolean movedSuspect;
    private Instant matchedAt;


    @Builder
    public PlaceMatchIssue(
            Long placeId, String placeAddress, String placeName,
            String kakaoAddress,
            Double nameSim, Double tokenOverlap, Double addrSim,
            Double distMeters, Double distScore, Double finalScore,
            MatchStatus matchStatus, Boolean renameSuspect, Boolean movedSuspect,
            Instant matchedAt
    ) {
        this.placeId = placeId;
        this.placeAddress = placeAddress;
        this.placeName = placeName;
        this.kakaoAddress = kakaoAddress;

        this.nameSim = nameSim;
        this.tokenOverlap = tokenOverlap;
        this.addrSim = addrSim;
        this.distMeters = distMeters;
        this.distScore = distScore;
        this.finalScore = finalScore;

        this.matchStatus = matchStatus;
        this.renameSuspect = renameSuspect;
        this.movedSuspect = movedSuspect;
        this.matchedAt = matchedAt != null ? matchedAt : Instant.now();
    }
}
