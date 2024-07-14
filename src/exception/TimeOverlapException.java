package exception;

public class TimeOverlapException extends RuntimeException {

    public TimeOverlapException(final String message) {
        super(message);
    }
}
