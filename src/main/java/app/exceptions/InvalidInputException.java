package app.exceptions;
public final class InvalidInputException extends BadRequestExceptions {
    public InvalidInputException(String message) {
        super(message);
    }
}
