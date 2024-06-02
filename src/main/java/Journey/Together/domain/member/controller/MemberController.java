package Journey.Together.domain.member.controller;

import Journey.Together.domain.member.dto.MemberReq;
import Journey.Together.domain.member.dto.MemberRes;
import Journey.Together.domain.member.service.MemberService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/member")
@Tag(name = "Member", description = "사용자 관련 API")
public class MemberController {
    private final MemberService memberService;

    @PatchMapping ("")
    public ApiResponse saveMemberInfo(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody MemberReq memberReq) {
        memberService.saveInfo(principalDetails.getMember(),memberReq);
        return ApiResponse.success(Success.UPDATE_USER_INFO_SUCCESS);
    }
}
