package Journey.Together.domain.member.service;

import Journey.Together.domain.member.dto.MyPageRes;
import Journey.Together.domain.member.dto.InterestDto;
import Journey.Together.domain.member.dto.MemberReq;
import Journey.Together.domain.member.dto.MemberRes;
import Journey.Together.domain.member.entity.Interest;
import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.repository.InterestRepository;
import Journey.Together.domain.member.repository.MemberRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.util.S3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final S3Client s3Client;


    public MyPageRes getMypage(Member member){
        Long date = Duration.between(member.getCreatedAt(), LocalDateTime.now()).toDays();
        return new MyPageRes(member.getNickname(), 0, date, s3Client.getUrl()+member.getProfileUuid()+"/profile");
    }

    @Transactional
    public void saveInfo(Member member, MultipartFile profileImage, MemberReq memberReq){
        // Validation
        memberRepository.findMemberByEmailAndDeletedAtIsNull(member.getEmail()).orElseThrow(()->new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION));
        //Business
        if (profileImage != null) {
            s3Client.update(member.getProfileUuid()+"/profile",profileImage);
            memberRepository.save(member);
        }
        if(memberReq == null){
            return;
        }
        if (memberReq.nickname() != null) {
            member.setNickname(memberReq.nickname());
        }
        if (memberReq.phone() != null) {
            member.setPhone(memberReq.phone());
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
    @Transactional
    public MemberRes findMemberInfo(Member member){
        // Validation
        memberRepository.findMemberByEmailAndDeletedAtIsNull(member.getEmail()).orElseThrow(()->new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION));
        //Business
        MemberRes memberRes = MemberRes.of(member, s3Client.getUrl()+member.getProfileUuid()+"/profile");
        //Response
        return memberRes;
    }
    @Transactional
    public void updateMemberInterest(Member member, InterestDto interestDto){
        // Validation
        memberRepository.findMemberByEmailAndDeletedAtIsNull(member.getEmail()).orElseThrow(()->new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION));
        Interest interest = interestRepository.findByMemberAndDeletedAtIsNull(member);
        //Business
        interest.update(interestDto);
    }
    @Transactional
    public InterestDto findMemberInterest(Member member){
        // Validation
        memberRepository.findMemberByEmailAndDeletedAtIsNull(member.getEmail()).orElseThrow(()->new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION));
        Interest interest = interestRepository.findByMemberAndDeletedAtIsNull(member);
        //Business
        InterestDto interestDto = InterestDto.of(
                interest.getIsPhysical(),
                interest.getIsHear(),
                interest.getIsVisual(),
                interest.getIsElderly(),
                interest.getIsChild()
        );
        //Response
        return interestDto;
    }

}
