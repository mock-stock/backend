package stock.mock_stock.exception;

public class InvalidStockCodeException extends RuntimeException {
    public InvalidStockCodeException(String message) {
        super(message);
    }
}
