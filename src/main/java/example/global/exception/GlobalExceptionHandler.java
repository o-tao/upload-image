package example.global.exception;

import example.global.exception.dto.CustomExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomApplicationException.class)
    public ResponseEntity<CustomExceptionResponse> responseTodoApplicationException(CustomApplicationException exception) {
        CustomExceptionResponse errorResult = new CustomExceptionResponse(
                exception.getExceptionHttpStatus(), exception.getExceptionMessage());
        return new ResponseEntity<>(errorResult, exception.getExceptionHttpStatus());
    }
}
