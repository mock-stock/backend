package stock.mock_stock.service;

import stock.mock_stock.dto.StockSearchResultDto;

import java.util.List;

public interface StockSearchService {

    public List<StockSearchResultDto> searchStocks(String searchQuery);
    public List<StockSearchResultDto> searchStocksWithHistory(String SearchQuery, Long userId);

}
