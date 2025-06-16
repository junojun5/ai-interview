package server.api.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import module.common.exception.ErrorCode;
import module.common.exception.UnAuthorizedException;
import module.common.utils.JwtUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import server.api.security.service.CustomUserDetailsService;

@RequiredArgsConstructor
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();
        List<String> whiteList = List.of("/login", "/css", "/js", "/favicon.ico", "/error", "/lib", "/oauth2", "/images", "/index.html");
        return whiteList.stream().anyMatch(uri::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");

        // 요청에 access token 없는 경우 exception 처리
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_MISSING_ACCESS_TOKEN);
        }

        String accessToken = bearerToken.substring("Bearer ".length());
        String userId = jwtUtils.getUserIdFromJwt(accessToken);

        // token subject에 아무것도 없는 경우 exception 처리
        if (!StringUtils.hasText(userId)) {
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_INVALID_TOKEN_SUBJECT);
        }

        // token subject 숫자로 변환 못하는 경우 exception 처리
        Long longUserId;
        try {
            longUserId = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_INVALID_TOKEN_SUBJECT);
        }

        if (jwtUtils.isValidateToken(accessToken)) {
            authenticateUser(longUserId);
        } else { // access token이 만료된 경우
            String reissueAccessToken = jwtUtils.reissueAccessToken(longUserId);
            response.setHeader("Authorization", "Bearer " + reissueAccessToken);  // 응답에 새 토큰 설정
            authenticateUser(longUserId);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(Long userId) {
        UserDetails userDetails = customUserDetailsService.loadUserById(userId);
        if (userDetails == null) {
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_USER_NOT_FOUND_EXCEPTION);
        }

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

