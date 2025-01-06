package stock.mock_stock.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StockInfoDto {
    private Long sid;
    private String stckName;
    private String stckCode;
    private Long stckCurPrice;
    private Long stckPrevClsDiffPrice;
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
