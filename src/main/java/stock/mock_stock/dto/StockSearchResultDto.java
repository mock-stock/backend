package stock.mock_stock.dto;

import lombok.Getter;

@Getter
public class StockSearchResultDto {
    private Long id;
    private String stckName;
    private Boolean isWatched;
}
