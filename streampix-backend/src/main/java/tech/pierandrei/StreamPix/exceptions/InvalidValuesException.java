package tech.pierandrei.StreamPix.exceptions;

/**
 * Exceção para retornar algum erro relacionado aos dados inseridos
 */
public class InvalidValuesException extends RuntimeException {

    public InvalidValuesException(String message) {
        super(message);
    }
}
