package server.api.auth.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import module.common.utils.JwtUtils;
import module.core.domain.user.User;
import module.core.domain.user.mysql.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.auth.dto.request.LoginRequestDto;
import server.api.auth.dto.response.LoginResponseDto;
import server.api.user.service.UserServiceUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;


    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        User user = UserServiceUtils.findUserByUsername(userRepository, loginRequestDto.getUsername());

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = loginRequestDto.toAuthentication();
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        List<String> tokenInfo = jwtUtils.createTokenInfo(user.getId());

        return LoginResponseDto.of(user.getId(), tokenInfo.get(0), tokenInfo.get(1));
    }
}
