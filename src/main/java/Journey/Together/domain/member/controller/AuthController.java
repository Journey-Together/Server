package Journey.Together.domain.member.controller;

import Journey.Together.domain.member.dto.LoginReq;
import Journey.Together.domain.member.dto.LoginRes;
import Journey.Together.domain.member.enumerate.LoginType;
import Journey.Together.domain.member.service.AuthService;
import Journey.Together.global.common.ApiResponse;
import Journey.Together.global.exception.Success;
import Journey.Together.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
    private final AuthService authService;
    @GetMapping
    public void login(HttpServletResponse response) throws IOException {
        response.sendRedirect("/login.html");
    }

    @Operation(summary = "로그인 API")
    @PostMapping("/sign-in")
    public ApiResponse<LoginRes> signIn(@RequestHeader("Authorization") String token,
                                        @RequestBody LoginReq loginReq) {
        return ApiResponse.success(Success.LOGIN_SUCCESS,authService.signIn(token,loginReq));
    }

    @Operation(summary = "로그아웃 API", description = "로그아웃된 JWT 블랙리스트 등록")
    @PostMapping("/sign-out")
    public ApiResponse<Void> signOut(HttpServletRequest request, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String token = request.getHeader("Authorization");
        authService.signOut(token, principalDetails.getMember());
        return ApiResponse.success(Success.SIGNOUT_SUCCESS);
    }
}
