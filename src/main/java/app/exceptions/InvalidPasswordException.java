package app.exceptions;

public final class InvalidPasswordException extends BadRequestExceptions {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
