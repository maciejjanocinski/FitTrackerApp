package app.exceptions;
public final class RecipeAlreadyAddedException extends BadRequestExceptions{
    public RecipeAlreadyAddedException(String message) {
        super(message);
    }
}

