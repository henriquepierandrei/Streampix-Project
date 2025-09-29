package tech.pierandrei.StreamPix.emailValidation;
public record SmtpResponseDTO (
    boolean success,
    String message,
    String token,
    String streamerId,
    String email
){}
