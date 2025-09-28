package tech.pierandrei.StreamPix.security.dtos;

public record RegisterRequestDto(
        String email,
        String cpf,
        String password,
        String nickname,
        String fullName
) {
}
