package tech.pierandrei.StreamPix.exceptions;

/**
 * Exceção para retornar algum erro relacionado a doação
 */
public class DonationNotFoundException extends RuntimeException{
    public DonationNotFoundException(String message) {
        super(message);
    }
}
