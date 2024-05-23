package Journey.Together.domain.member.controller;

import Journey.Together.domain.member.dto.MemberRes;
import Journey.Together.domain.member.service.MemberService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/member")
@Tag(name = "Member", description = "사용자 관련 API")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("")
    public ApiResponse<MemberRes> getMember() {
        return ApiResponse.success(Success.LOGIN_SUCCESS);
    }
}
