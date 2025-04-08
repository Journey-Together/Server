package Journey.Together.domain.member.dto;

import Journey.Together.domain.member.entity.Member;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record MemberRes(
        String name,
        String nickname,
        String phone,
        String profileImage,
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
    private static String checkName(String name) {
        if(name == null) {
            return "저장된 이름이 없습니다";
        }
        return name;
    }
    public static MemberRes of(Member member,String profileUrl){

        return MemberRes.builder()
                .name(checkName(member.getName()))
                .nickname(member.getNickname())
                .phone(member.getPhone())
                .profileImage(profileUrl)
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
