package module.core.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import module.common.enums.ProviderType;
import module.common.enums.RoleType;
import module.core.domain.common.AuditingTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class User extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Size(max = 30)
    private String username;

    @Size(max = 100)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleType role;

    public static User newInstance(String username, String password, ProviderType providerType, RoleType role) {
        return builder()
            .username(username)
            .password(password)
            .providerType(providerType)
            .role(role)
            .build();
    }
}
