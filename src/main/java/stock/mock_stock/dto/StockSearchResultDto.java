package stock.mock_stock.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StockSearchResultDto {
    @NotNull
    private Long sid;
    @NotNull
    private String stckName;
    @NotNull
    private String stckCode;


    public StockSearchResultDto(Long sid, String stckName, String stckCode){
        this.sid = sid;
        this.stckName = stckName;
        this.stckCode = stckCode;

    }
}
