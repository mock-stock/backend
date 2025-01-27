package stock.mock_stock.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StockSearchResultDto {
    private Long sid;
    private String stckName;
    private String stckCode;


    public StockSearchResultDto(Long sid, String stckName, String stckCode){
        this.sid = sid;
        this.stckName = stckName;
        this.stckCode = stckCode;

    }
}
