package app.exceptions;
public final class ProductNotFoundException extends BadRequestExceptions{
    public ProductNotFoundException(String message) {
        super(message);
    }
}