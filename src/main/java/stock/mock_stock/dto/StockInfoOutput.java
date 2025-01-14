package stock.mock_stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class StockInfoOutput {

    @JsonProperty("output")
    private StockKisDto stockKisDto;

    @JsonProperty("output2")
    private List<StockKisHistoryDto> stockKisHistoryDto;
}
