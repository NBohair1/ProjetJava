package exception;

public class ComposantException extends Exception {
    
    public ComposantException(String message) {
        super(message);
    }
    
    public ComposantException(String message, Throwable cause) {
        super(message, cause);
    }
}
