package tech.pierandrei.StreamPix.security.dtos;

public record AuthResponseDto(
        String token,
        long tokenExpireAt,
        String refreshToken,
        long refreshTokenExpireAt

        ) {
}
