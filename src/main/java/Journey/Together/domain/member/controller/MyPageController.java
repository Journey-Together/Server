package Journey.Together.domain.member.controller;


import Journey.Together.domain.member.dto.MyPageRes;
import Journey.Together.domain.member.service.MemberService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/mypage")
@Tag(name = "Mypage", description = "마이페이지 관련 API")
public class MyPageController {

    private final MemberService memberService;

    @GetMapping
    public ApiResponse<MyPageRes> getMyPage(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(Success.GET_MYPAGE_SUCCESS, memberService.getMypage(principalDetails.getMemberId()));
    }

}
