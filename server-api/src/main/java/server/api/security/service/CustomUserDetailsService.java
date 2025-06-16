package server.api.security.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import module.core.domain.user.User;
import module.core.domain.user.mysql.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import server.api.security.details.CustomUserDetails;
import server.api.security.model.CustomUserInfo;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = Optional.ofNullable(userRepository.findUserByUsername(username))
            .orElseThrow(
                () -> new UsernameNotFoundException("User Not Found with userEmail: " + username));

        return new CustomUserDetails(CustomUserInfo.of(user), null);
    }

    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        return new CustomUserDetails(CustomUserInfo.of(user), null);
    }
}
