package server.api.security.model;

import lombok.AccessLevel;
import lombok.Builder;
import module.common.enums.RoleType;
import module.core.domain.user.User;

@Builder(access = AccessLevel.PRIVATE)
public record CustomUserInfo(
    Long id,
    String username,
    String password,
    RoleType role
) {
    public static CustomUserInfo of(User user) {
        return CustomUserInfo.builder()
            .id(user.getId())
            .username(user.getUsername())
            .password(user.getPassword())
            .role(user.getRole())
            .build();
    }
}

