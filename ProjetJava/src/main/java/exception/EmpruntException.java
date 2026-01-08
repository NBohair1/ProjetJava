package exception;

public class EmpruntException extends Exception {

    public EmpruntException(String message) {
        super(message);
    }
    
    public EmpruntException(String message, Throwable cause) {
        super(message, cause);
    }
}
