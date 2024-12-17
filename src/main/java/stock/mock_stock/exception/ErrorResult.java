package stock.mock_stock.exception;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ErrorResult {

    private int status;
    private String error;
    private String message;

    public ErrorResult(int status, String error, String message){
        this.status = status;
        this.error = error;
        this.message = message;
    }

}
