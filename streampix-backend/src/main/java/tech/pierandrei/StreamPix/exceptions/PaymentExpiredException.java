package tech.pierandrei.StreamPix.exceptions;

/**
 * Exceção para retornar o erro de pagamento expirado
 */
public class PaymentExpiredException extends RuntimeException{
    public PaymentExpiredException(String message) {
        super(message);
    }
}
