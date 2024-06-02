package Journey.Together.domain.member.dto;

import Journey.Together.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;
import lombok.Builder;

@Builder
public record MemberRes(
        String name,
        String phone,
        String profileUrl,
        String bloodType,

        String birth,
        String disease,
        String allergy,
        String medication,
        String part1_rel,
        String part1_phone,
        String part2_rel,
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
