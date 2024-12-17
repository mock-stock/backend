package stock.mock_stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StockInfoOutput {

    @JsonProperty("output")
    private StockKisDto StockKisDto;
}
