package tech.pierandrei.StreamPix.security.dtos;

public record AuthRequestDto(
        String email,
        String password
) {
}
