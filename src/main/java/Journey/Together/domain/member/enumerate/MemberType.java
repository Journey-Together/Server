package Journey.Together.domain.member.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
    GENERAL("ROLE_GENERAL", "일반"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String role;
    private final String title;
}
