package stock.mock_stock.dto;

import lombok.Data;

@Data
public class StockSearchResultDto {
    private Long id;
    private String stckName;
    private Boolean isWatched;

    public StockSearchResultDto(Long id, String stckName, Boolean isWatched){
        this.id = id;
        this.stckName = stckName;
        this.isWatched = isWatched;
    }
}
