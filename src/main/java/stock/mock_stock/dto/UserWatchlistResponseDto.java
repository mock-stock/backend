package stock.mock_stock.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserWatchlistResponseDto {
    private Long wid;
    private String stockCode;

    public UserWatchlistResponseDto(Long wid, String stockCode) {
        this.wid = wid;
        this.stockCode = stockCode;
    }

    public Long getWid() { return wid; }
    public String getStockCode() { return stockCode; }
}
