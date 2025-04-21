package server.api.user.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import module.common.exception.ErrorCode;
import module.common.exception.NotFoundException;
import module.core.domain.user.User;
import module.core.domain.user.mysql.UserRepository;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserServiceUtils {
    public static User findUserByUsername(UserRepository userRepository, String userName) {
        User user = userRepository.findUserByUsername(userName);
        if (user == null) {
            throw new NotFoundException(
                String.format("존재하지 않는 유저 (%s) 입니다.", userName),
                ErrorCode.NOT_FOUND_USER_EXCEPTION
            );
        }

        return user;
    }
}
