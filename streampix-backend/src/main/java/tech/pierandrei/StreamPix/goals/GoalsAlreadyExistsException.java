package tech.pierandrei.StreamPix.goals;

public class GoalsAlreadyExistsException extends RuntimeException{
    public GoalsAlreadyExistsException(String message) {
        super(message);
    }
}
