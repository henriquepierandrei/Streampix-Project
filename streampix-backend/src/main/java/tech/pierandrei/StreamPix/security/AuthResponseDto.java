package tech.pierandrei.StreamPix.security;

public record AuthResponseDto(
        String token,
        long tokenExpireAt,
        String refreshToken,
        long refreshTokenExpireAt

        ) {
}
