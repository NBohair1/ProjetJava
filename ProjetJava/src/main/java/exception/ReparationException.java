package exception;

public class ReparationException extends Exception {

    public ReparationException(String message) {
        super(message);
    }
    
    public ReparationException(String message, Throwable cause) {
        super(message, cause);
    }
}
