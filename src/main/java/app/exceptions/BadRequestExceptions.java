package app.exceptions;

public sealed class BadRequestExceptions extends RuntimeException
        permits
        InvalidInputException,
        InvalidPasswordException,
        ProductsApiException,
        RecipeAlreadyAddedException{
    public BadRequestExceptions(String message) {
        super(message);
    }
}
