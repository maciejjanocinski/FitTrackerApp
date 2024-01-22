package app.exceptions;
public final class ProductsApiException extends RuntimeException{
    public ProductsApiException(String message) {
        super(message);
    }
}
