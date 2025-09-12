package tech.pierandrei.StreamPix.security;

public record RegisterRequestDto(
        String email,
        String password,
        String name
) {
}
