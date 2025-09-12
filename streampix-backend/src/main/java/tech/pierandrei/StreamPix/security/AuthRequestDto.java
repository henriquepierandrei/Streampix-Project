package tech.pierandrei.StreamPix.security;

public record AuthRequestDto(
        String email,
        String password
) {
}
