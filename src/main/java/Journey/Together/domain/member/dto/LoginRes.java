package Journey.Together.domain.member.dto;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.enumerate.LoginType;
import Journey.Together.domain.member.enumerate.MemberType;
import Journey.Together.global.security.jwt.dto.TokenDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder

public record LoginRes(
        @NotNull Long memberId,
        @NotNull String email,
        @NotNull String name,
        @NotNull String nickname,
        String profileUuid,
        MemberType memberType,
        LoginType loginType,
        @NotNull String accessToken,
        @NotNull String refreshToken
) {
    public static LoginRes of(Member member, TokenDto tokenDto) {
        return LoginRes.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .profileUuid(member.getProfileUuid())
                .memberType(member.getMemberType())
                .loginType(member.getLoginType())
                .accessToken(tokenDto.accessToken())
                .refreshToken(tokenDto.refreshToken())
                .build();
    }
}
