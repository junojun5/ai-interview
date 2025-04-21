package server.api.auth.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class LoginResponseDto {
    private Long userId;
    private TokenInfo tokenInfo;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class TokenInfo {
        private String accessToken;
        private String refreshToken;
    }

    public static LoginResponseDto of(Long userId, String accessToken, String refreshToken) {
        return LoginResponseDto.builder()
            .userId(userId)
            .tokenInfo(
                TokenInfo.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build()
            )
            .build();
    }
}
