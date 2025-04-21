package module.core.domain.user.mysql;

import module.core.domain.user.User;

public interface UserRepositoryCustom {
    User findUserByUsername(String username);
}
