package Journey.Together.domain.member.dto;

import Journey.Together.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;
import lombok.Builder;

@Builder
public record MemberRes(
        String name,
        @Null
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String phone,
        @Null
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String profileUrl,
        @Null
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String bloodType,
        @Null
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String birth,
        @Null
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String disease,
        @Null
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String allergy,
        @Null
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String medication,
        @Null
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String part1_rel,
        @Null
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String part1_phone,
        @Null
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String part2_rel,
        @Null
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String part2_phone
) {
    public static MemberRes of(Member member){
        return MemberRes.builder()
                .name(member.getName())
                .phone(member.getPhone())
                .profileUrl(member.getProfileUrl())
                .bloodType(member.getBloodType())
                .birth(member.getBirth())
                .disease(member.getDisease())
                .allergy(member.getAllergy())
                .medication(member.getMedication())
                .part1_rel("ee")
                .part1_phone("pp")
                .part2_rel("ee")
                .part2_phone("pp")
                .build();

    }
}
