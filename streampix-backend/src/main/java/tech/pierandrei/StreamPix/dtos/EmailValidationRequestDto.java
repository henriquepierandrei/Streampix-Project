package tech.pierandrei.StreamPix.dtos;

public record EmailValidationRequestDto(
        String code,
        Long streamerId,
        String sessionToken
) {}
