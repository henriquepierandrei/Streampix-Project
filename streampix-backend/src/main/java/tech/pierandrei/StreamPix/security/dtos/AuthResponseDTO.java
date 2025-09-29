package tech.pierandrei.StreamPix.security.dtos;

public record AuthResponseDTO(
        String token,
        long tokenExpireAt,
        String refreshToken,
        long refreshTokenExpireAt

        ) {
}
