package module.core.domain;


import java.lang.reflect.Field;
import module.common.enums.ProviderType;
import module.common.enums.RoleType;
import module.core.domain.user.User;

public enum UserFixture {
    BASIC_USER(1L, "basic_user@gmail.com", "password", ProviderType.BASIC, RoleType.ROLE_USER),
    BASIC_ADMIN(2L, "basic_admin@gmail.com", "password", ProviderType.BASIC, RoleType.ROLE_ADMIN),
    GOOGLE_USER(3L, "google_user@gmail.com", "password", ProviderType.GOOGLE, RoleType.ROLE_USER),
    GOOGLE_ADMIN(4L, "google_admin@gmail.com", "password", ProviderType.GOOGLE, RoleType.ROLE_ADMIN),
    NAVER_USER(5L, "naver_user@gmail.com", "password", ProviderType.NAVER, RoleType.ROLE_USER),
    NAVER_ADMIN(6L, "naver_admin@gmail.com", "password", ProviderType.NAVER, RoleType.ROLE_ADMIN);

    private final Long id;
    private final String username;
    private final String password;
    private final ProviderType providerType;
    private final RoleType role;

    UserFixture(Long id, String username, String password, ProviderType providerType,
        RoleType role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.providerType = providerType;
        this.role = role;
    }

    public User getUser() {
        User user = User.newInstance(this.username, this.password, this.providerType, this.role);

        // 리플렉션 또는 직접 접근을 통해 ID 설정
        setId(user, this.id);
        return user;
    }

    private void setId(User user, Long id) {
        try {
            Field field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set id on User", e);
        }
    }
}
