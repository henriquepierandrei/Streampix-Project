package tech.pierandrei.StreamPix.security.dtos;

public record AuthRequestDTO(
        String email,
        String password
) {
}
