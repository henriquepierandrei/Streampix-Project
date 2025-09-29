package tech.pierandrei.StreamPix.logDonations;

/**
 * Exceção para retornar o erro de pagamento expirado
 */
public class PaymentExpiredException extends RuntimeException{
    public PaymentExpiredException(String message) {
        super(message);
    }
}
