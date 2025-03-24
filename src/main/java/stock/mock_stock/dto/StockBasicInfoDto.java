package stock.mock_stock.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StockBasicInfoDto {
    private Long sid;
    private String stckName;
    private String stckCode;
}
