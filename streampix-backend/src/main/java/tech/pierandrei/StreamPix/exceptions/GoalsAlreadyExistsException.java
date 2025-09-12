package tech.pierandrei.StreamPix.exceptions;

public class GoalsAlreadyExistsException extends RuntimeException{
    public GoalsAlreadyExistsException(String message) {
        super(message);
    }
}
