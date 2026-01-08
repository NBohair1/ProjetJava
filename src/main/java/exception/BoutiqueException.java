package exception;

public class BoutiqueException extends Exception {

    public BoutiqueException(String message) {
        super(message);
    }

    public BoutiqueException(String message, Throwable cause) {
        super(message, cause);
    }
}
