package stock.mock_stock.exception;

public class NotFoundStockException extends RuntimeException {
    public NotFoundStockException(String message) {
        super(message);
    }
}
