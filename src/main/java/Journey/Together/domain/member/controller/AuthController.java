package Journey.Together.domain.member.controller;

import Journey.Together.domain.member.dto.LoginRes;
import Journey.Together.domain.member.enumerate.LoginType;
import Journey.Together.domain.member.service.AuthService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
    private final AuthService authService;
    @GetMapping
    public String login(){
        return "login";
    }

    @Operation(summary = "로그인 API", description = "카카오 로그인 페이지로 리다이렉트되어 카카오 로그인을 수행할 수 있도록 안내")
    @PostMapping("/sign-in")
    public ApiResponse<LoginRes> signIn(@RequestParam(name = "code") String code, @RequestBody String type) {
        return ApiResponse.success(Success.LOGIN_SUCCESS,authService.signIn(code,LoginType.valueOf(type)));
    }

    @Operation(summary = "로그아웃 API", description = "로그아웃된 JWT 블랙리스트 등록")
    @PostMapping("/sign-out")
    public ApiResponse<Void> signOut(HttpServletRequest request, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String token = request.getHeader("Authorization");
        authService.signOut(token, principalDetails.getMember());
        return ApiResponse.success(Success.SIGNOUT_SUCCESS);
    }
}
