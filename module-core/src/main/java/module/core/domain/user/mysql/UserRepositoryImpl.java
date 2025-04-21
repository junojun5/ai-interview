package module.core.domain.user.mysql;

import static module.core.domain.user.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import module.core.domain.user.User;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public User findUserByUsername(String username) {
        return queryFactory
            .selectFrom(user)
            .where(user.username.eq(username))
            .fetchOne();
    }
}
