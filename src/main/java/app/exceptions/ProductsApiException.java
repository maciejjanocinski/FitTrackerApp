package app.exceptions;
public final class ProductsApiException extends BadRequestExceptions{
    public ProductsApiException(String message) {
        super(message);
    }
}
