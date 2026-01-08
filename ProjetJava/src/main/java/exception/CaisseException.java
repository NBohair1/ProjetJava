package exception;

public class CaisseException extends Exception {

    public CaisseException(String message) {
        super(message);
    }
    
    public CaisseException(String message, Throwable cause) {
        super(message, cause);
    }
}
