package Journey.Together.global.security;

import Journey.Together.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class PrincipalDetails implements UserDetails, OAuth2User {

    @Getter
    private final Member member;
    private Map<String, Object> attributes;

    // 일반 로그인
    public PrincipalDetails(Member member) {
        this.member = member;
    }

    // OAuth 로그인
    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    // 권한 정보 반환 (GENERAL, ADMIN 중 하나)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(String.valueOf(member.getMemberType())));

        return authorities;
    }

    // 사용자의 비밀번호 반환
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    // 사용자의 이름 반환
    @Override
    public String getUsername() {
        return member.getName();
    }

    // 계정이 잠기지 않았으므로 true 반환
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 패스워드가 만료되지 않았으므로 true 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계속 사용 가능한 것이기에 true 반환
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
