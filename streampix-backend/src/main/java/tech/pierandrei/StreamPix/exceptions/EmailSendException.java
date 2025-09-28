package tech.pierandrei.StreamPix.exceptions;


/**
 * Exceção para retornar algum erro relacionado ao envio de email
 */
public class EmailSendException extends RuntimeException{

    public EmailSendException(String message) {
        super(message);
    }
}
