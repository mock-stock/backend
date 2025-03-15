package stock.mock_stock.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResult> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResult errorResult = new ErrorResult(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResult> handleNotFoundException(EntityNotFoundException e){
       ErrorResult errorResult = new ErrorResult(HttpStatus.NOT_FOUND.value(), "Not Found" ,e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResult> handleAccessDeniedException(AccessDeniedException e) {
        ErrorResult errorResult = new ErrorResult(HttpStatus.FORBIDDEN.value(), "Forbidden" ,e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResult);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResult> handleInternalServerError(RuntimeException e) {
        ErrorResult errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }



    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", ex.getReason());
        responseBody.put("status", ex.getStatusCode().value());

        return new ResponseEntity<>(responseBody, ex.getStatusCode());
    }

}
