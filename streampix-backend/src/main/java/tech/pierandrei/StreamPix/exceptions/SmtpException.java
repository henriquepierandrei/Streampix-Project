package tech.pierandrei.StreamPix.exceptions;

import org.springframework.http.HttpStatus;

// Classe base para todas as exceptions SMTP
public abstract class SmtpException extends RuntimeException {
    private final HttpStatus status;

    public SmtpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public SmtpException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    // ============ CLASSES INTERNAS ESTÁTICAS ============

    // Código expirado
    public static class CodeExpired extends SmtpException {
        public CodeExpired(String message) {
            super(message, HttpStatus.GONE); // 410
        }
    }

    // Máximo de tentativas excedido
    public static class MaxAttemptsExceeded extends SmtpException {
        public MaxAttemptsExceeded(String message) {
            super(message, HttpStatus.TOO_MANY_REQUESTS); // 429
        }
    }

    // Tempo de espera necessário
    public static class WaitTimeRequired extends SmtpException {
        private final long waitTimeMinutes;

        public WaitTimeRequired(String message, long waitTimeMinutes) {
            super(message, HttpStatus.TOO_MANY_REQUESTS); // 429
            this.waitTimeMinutes = waitTimeMinutes;
        }

        public long getWaitTimeMinutes() {
            return waitTimeMinutes;
        }
    }

    // Código inválido
    public static class InvalidCode extends SmtpException {
        public InvalidCode(String message) {
            super(message, HttpStatus.BAD_REQUEST); // 400
        }
    }

    // Sessão inválida
    public static class InvalidSession extends SmtpException {
        public InvalidSession(String message) {
            super(message, HttpStatus.UNAUTHORIZED); // 401
        }
    }

    // Código já validado
    public static class CodeAlreadyValidated extends SmtpException {
        public CodeAlreadyValidated(String message) {
            super(message, HttpStatus.CONFLICT); // 409
        }
    }

    // Solicitação não encontrada
    public static class RequestNotFound extends SmtpException {
        public RequestNotFound(String message) {
            super(message, HttpStatus.NOT_FOUND); // 404
        }
    }
}