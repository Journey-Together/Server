package Journey.Together.domain.place.dto.match;

import Journey.Together.domain.place.enumerated.MatchStatus;
import lombok.Builder;

@Builder
public record MatchDecision(
        MatchStatus status,
        double finalScore,

        String bestName,
        String bestAddress,
        String bestSourceId,
        Double bestLon,
        Double bestLat,

        double nameSim,
        double tokenOverlap,
        double addrSim,
        double distMeters,
        double distScore,

        boolean renameSuspect,
        boolean movedSuspect
) {
}
