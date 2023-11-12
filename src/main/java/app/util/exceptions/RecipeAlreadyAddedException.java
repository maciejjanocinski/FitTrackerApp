package app.util.exceptions;

public class RecipeAlreadyAddedException extends RuntimeException{
    public RecipeAlreadyAddedException(String message) {
        super(message);
    }
}

