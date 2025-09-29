package tech.pierandrei.StreamPix.emailValidation;

public record EmailValidationRequestDTO(
        String code,
        String streamerId,
        String sessionToken
) {}
