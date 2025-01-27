package stock.mock_stock.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StockInfoDto {

    @NotNull
    private Long sid;
    @NotNull
    private String stckName;
    @NotNull
    private String stckCode;
    @NotNull
    private Long stckCurPrice;
    @NotNull
    private Long stckPrevClsDiffPrice;
    @NotNull
    private Double stckPrevClsDiffPercent;
    //TODO: Double, Long 타입 맞는지 추후 체크

    public StockInfoDto(Long sid, String stckName, String stckCode, Long stckCurPrice, Long stckPrevClsDiffPrice, Double stckPrevClsDiffPercent){
        this.sid = sid;
        this.stckName = stckName;
        this.stckCode = stckCode;
        this.stckCurPrice = stckCurPrice;
        this.stckPrevClsDiffPrice = stckPrevClsDiffPrice;
        this.stckPrevClsDiffPercent = stckPrevClsDiffPercent;
    }
}
