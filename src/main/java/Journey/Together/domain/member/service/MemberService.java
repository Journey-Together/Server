package Journey.Together.domain.member.service;

import Journey.Together.domain.member.dto.MemberReq;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.repository.MemberRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void saveInfo(Member member,MemberReq memberReq){
        memberRepository.findMemberByEmailAndDeletedAtIsNull(member.getEmail()).orElseThrow(()->new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION));
        if (memberReq.name() != null) {
            member.setName(memberReq.name());
        }
        if (memberReq.phone() != null) {
            member.setPhone(memberReq.phone());
        }
        if (memberReq.profileUrl() != null) {
            member.setProfileUrl(memberReq.profileUrl());
        }
        if (memberReq.bloodType() != null) {
            member.setBloodType(memberReq.bloodType());
        }
        if (memberReq.birth() != null) {
            member.setBirth(memberReq.birth());
        }
        if (memberReq.disease() != null) {
            member.setDisease(memberReq.disease());
        }
        if (memberReq.allergy() != null) {
            member.setAllergy(memberReq.allergy());
        }
        if (memberReq.medication() != null) {
            member.setMedication(memberReq.medication());
        }
        if (memberReq.part1_rel() != null) {
            member.setPart1Rel(memberReq.part1_rel());
        }
        if (memberReq.part1_phone() != null) {
            member.setPart1Phone(memberReq.part1_phone());
        }
        if (memberReq.part2_rel() != null) {
            member.setPart2Rel(memberReq.part2_rel());
        }
        if (memberReq.part2_phone() != null) {
            member.setPart2Phone(memberReq.part2_phone());
        }

        memberRepository.save(member);
    }

}
