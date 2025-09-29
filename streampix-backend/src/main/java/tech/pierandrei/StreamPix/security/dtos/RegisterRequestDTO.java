package tech.pierandrei.StreamPix.security.dtos;

public record RegisterRequestDTO(
        String email,
        String cpf,
        String password,
        String nickname,
        String fullName
) {
}
