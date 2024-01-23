package app.exceptions;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

import static app.util.Utils.USER_ALREADY_EXISTS_MESSAGE;
import static app.util.Utils.WRONG_CREDENTIALS_MESSAGE;

@RestControllerAdvice
public class RuntimeExceptionHandler {

    @ExceptionHandler(BadRequestExceptions.class)
    private ResponseEntity<String> handleBadRequestExceptions(BadRequestExceptions ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        String res = ex.getConstraintViolations().iterator().next().getMessage();
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductsApiException.class)
    private ResponseEntity<String> handleProductsApiException(ProductsApiException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    private ResponseEntity<String> handleProductNotFoundException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    private ResponseEntity<String> handleUnexpectedTypeException(UnexpectedTypeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    private ResponseEntity<String> handleBadCredentialsException() {
        return new ResponseEntity<>(WRONG_CREDENTIALS_MESSAGE, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    private ResponseEntity<String> handleSQLIntegrityConstraintViolationException() {
        return new ResponseEntity<>(USER_ALREADY_EXISTS_MESSAGE, HttpStatus.BAD_REQUEST);
    }


}