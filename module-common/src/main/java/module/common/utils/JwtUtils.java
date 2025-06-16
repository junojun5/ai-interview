package module.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import module.common.contstant.RedisKey;
import module.common.exception.ErrorCode;
import module.common.exception.UnAuthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Slf4j
@Component
public class JwtUtils {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Key secretKey;

    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(10);
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(180);
    private static final Duration REDIS_EXPIRED_DURATION = Duration.ofMillis(1);

    public JwtUtils(
        @Value("${jwt.secret}") String secretKey,
        RedisTemplate<String, Object> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // access, refresh token 발급
    public List<String> createTokenInfo(Long userId) {
        String accessToken = issueAccessToken(userId);
        String refreshToken = issueRefreshToken();

        redisTemplate.opsForValue().set(
            RedisKey.REFRESH_TOKEN + userId,
            refreshToken,
            REFRESH_TOKEN_DURATION.toMillis(),
            TimeUnit.MILLISECONDS
        );

        return List.of(accessToken, refreshToken);
    }

    // access token 재발급
    public String reissueAccessToken(Long userId) {
        String refreshToken = (String) redisTemplate.opsForValue().get(RedisKey.REFRESH_TOKEN + userId);

        if (!StringUtils.hasText(refreshToken) || !isValidateToken(refreshToken)) {
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_EXPIRED_REFRESH_TOKEN_EXCEPTION);
        }

        return issueAccessToken(userId);
    }

    // refresh token 만료
    public void expireRefreshToken(Long userId) {
        redisTemplate.opsForValue().set(
            RedisKey.REFRESH_TOKEN + userId,
            "",
            REDIS_EXPIRED_DURATION.toMillis(),
            TimeUnit.MILLISECONDS
        );
    }

    // 토큰 검증
    public boolean isValidateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        Claims claims = parseClaims(token);
        return claims.getExpiration().after(new Date());
    }

    // access token에서 userId(subject) 추출
    public String getUserIdFromJwt(String accessToken) {
        return parseClaims(accessToken).getSubject();
    }

    // --- 내부 토큰 발급 로직 ---

    private String issueAccessToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_TOKEN_DURATION.toMillis());

        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setSubject(String.valueOf(userId))
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact();
    }

    private String issueRefreshToken() {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + REFRESH_TOKEN_DURATION.toMillis());

        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | DecodingException e) {
            log.warn("Invalid JWT Token", e);
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_INVALID_TOKEN_EXCEPTION);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT Token", e);
            return e.getClaims(); // 유효성은 따로 검사함
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT Token", e);
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_UNSUPPORTED_TOKEN_EXCEPTION);
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty.", e);
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_EMPTY_TOKEN_EXCEPTION);
        } catch (Exception e) {
            log.error("Unhandled JWT exception", e);
            throw new UnAuthorizedException(ErrorCode.UNAUTHORIZED_INVALID_TOKEN_EXCEPTION);
        }
    }
}

