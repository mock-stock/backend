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
    private Long stckCurPrice; // NOTE: 현재가
    @NotNull
    private Long stckPrevClsDiffPrice; // NOTE: 전일 종가대비 현재가 차익 금액
    @NotNull
    private Double stckPrevClsDiffPercent; // NOTE: 전일 종가대비 현재가 차익 퍼센트
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
