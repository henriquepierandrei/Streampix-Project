package tech.pierandrei.StreamPix.smtp;

import org.springframework.http.HttpStatus;


public record SmtpResponseDto (
    HttpStatus httpStatus,
    String message,
    Boolean sentEmail

){}
