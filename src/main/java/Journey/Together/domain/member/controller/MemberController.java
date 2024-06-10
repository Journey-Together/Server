package Journey.Together.domain.member.controller;

import Journey.Together.domain.dairy.dto.PlanReviewReq;
import Journey.Together.domain.member.dto.InterestDto;
import Journey.Together.domain.member.dto.MemberReq;
import Journey.Together.domain.member.dto.MemberRes;
import Journey.Together.domain.member.service.MemberService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/member")
@Tag(name = "Member", description = "사용자 관련 API")
public class MemberController {
    private final MemberService memberService;

    @PatchMapping ("")
    public ApiResponse saveMemberInfo(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestPart(required = false) MultipartFile profileImage, @RequestPart(required = false) MemberReq memberReq) {
        memberService.saveInfo(principalDetails.getMember(),profileImage,memberReq);
        return ApiResponse.success(Success.UPDATE_USER_INFO_SUCCESS);
    }

    @GetMapping("")
    public ApiResponse<MemberRes> findMemberInfo(@AuthenticationPrincipal PrincipalDetails principalDetails){
        MemberRes memberRes = memberService.findMemberInfo(principalDetails.getMember());
        return ApiResponse.success(Success.GET_MYPAGE_SUCCESS,memberRes);
    }

    @PatchMapping("/interest-type")
    public ApiResponse updateInterestType(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody InterestDto interestDto) {
        memberService.updateMemberInterest(principalDetails.getMember(),interestDto);
        return ApiResponse.success(Success.UPDATE_USER_INFO_SUCCESS);
    }

    @GetMapping("/interest-type")
    public ApiResponse<InterestDto> findInterestType(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        InterestDto interestDto = memberService.findMemberInterest(principalDetails.getMember());
        return ApiResponse.success(Success.GET_USER_INTEREST_SUCCESS,interestDto);
    }
}
