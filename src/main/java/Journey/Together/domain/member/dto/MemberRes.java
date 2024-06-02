package Journey.Together.domain.member.dto;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.enumerate.RelationType;
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
        RelationType part1_rel,
        String part1_phone,
        RelationType part2_rel,
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
                .part1_rel(member.getPart1Rel())
                .part1_phone(member.getPart1Phone())
                .part2_rel(member.getPart2Rel())
                .part2_phone(member.getPart2Phone())
                .build();

    }
}
