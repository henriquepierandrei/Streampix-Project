package tech.pierandrei.StreamPix.emailValidation;

import org.springframework.http.HttpStatus;
public record SmtpResponseDTO (
    HttpStatus httpStatus,
    String message,
    Boolean sentEmail,
    String sessionToken
){}
