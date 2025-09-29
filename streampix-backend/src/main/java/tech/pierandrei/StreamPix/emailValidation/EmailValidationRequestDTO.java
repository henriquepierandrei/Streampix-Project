package tech.pierandrei.StreamPix.emailValidation;

public record EmailValidationRequestDTO(
        String code,
        Long streamerId,
        String sessionToken
) {}
