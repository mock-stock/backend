package stock.mock_stock.dto;

import lombok.Data;

import java.util.List;

@Data
public class StockSubscribeRequestDto {
    private String action;
    private List<String> ids;
}
