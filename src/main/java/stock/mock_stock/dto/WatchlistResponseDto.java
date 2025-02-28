package stock.mock_stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class WatchlistResponseDto {
    private Long wid;
    private Long sid;
    private String stckName;
    private String stckCode;
    private Long stckCurPrice;
    private Long stckPrevClsDiffPrice;
    private Double stckPrevClsDiffPercent;
}
