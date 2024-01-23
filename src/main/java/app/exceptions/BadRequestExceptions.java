package app.exceptions;

public sealed class BadRequestExceptions extends RuntimeException
        permits
        InvalidInputException,
        InvalidPasswordException,
        RecipeAlreadyAddedException,
        ProductNotFoundException {
    public BadRequestExceptions(String message) {
        super(message);
    }
}
