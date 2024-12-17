package stock.mock_stock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidStockCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 상태 코드
    public ErrorResult handleInvalidStockCodeException(InvalidStockCodeException e){
        return new ErrorResult(HttpStatus.BAD_REQUEST.value(), "Bad Request" ,e.getMessage());
    }

    @ExceptionHandler(NotFoundStockException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404 상태 코드
    public ErrorResult handleNotFoundStockException(NotFoundStockException e){
        return new ErrorResult(HttpStatus.NOT_FOUND.value(), "Not Found" ,e.getMessage());
    }

}
