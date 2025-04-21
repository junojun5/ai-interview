package server.api.security.details;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import server.api.security.model.CustomUserInfo;

public record CustomUserDetails(
    CustomUserInfo customUserInfo,
    Map<String, Object> attributes
) implements UserDetails {
    // 사용자에게 부여된 권한을 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority(customUserInfo.role().name())
        );
    }

    // 인증에 사용되는 암호 반환
    @Override
    public String getPassword() {
        return customUserInfo.password();
    }

    // 인증하는데 사용되는 username 반환
    @Override
    public String getUsername() {
        return customUserInfo.username();
    }

    // 사용자 계정만료여부 반환 (true: 만료 X false: 만료 O)
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    // 사용자 계정잠금여부 반환 (true: 잠금 X false: 잠금 O)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 사용자 자격증명만료 여부 반환 (true: 패스워드 만료 X, false: 패스워드 만료 O)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 사용자 사용가능여부 반환 (true: 사용가능 O, false: 사용가능 X)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
