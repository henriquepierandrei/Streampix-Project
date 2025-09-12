package tech.pierandrei.StreamPix.exceptions;

/**
 * Exceção para retornar erro na API criada para gerar a voz
 */
public class APIVoiceException extends RuntimeException{
    public APIVoiceException(String message) {
        super(message);
    }
}
