package Journey.Together.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InterestDto (
       Boolean isPhysical,
       Boolean isHear,
       Boolean isVisual,
       Boolean isElderly,
       Boolean isChild
){
    public static InterestDto of( Boolean isPhysical,
                                  Boolean isHear,
                                  Boolean isVisual,
                                  Boolean isElderly,
                                  Boolean isChild){
            return InterestDto.builder()
                    .isPhysical(isPhysical)
                    .isHear(isHear)
                    .isChild(isChild)
                    .isElderly(isElderly)
                    .isVisual(isVisual)
                    .build();
    }

}
