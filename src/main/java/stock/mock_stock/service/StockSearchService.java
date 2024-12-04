package stock.mock_stock.service;

import stock.mock_stock.dto.StockSearchResultDto;

import java.util.List;

public interface StockSearchService {

    public List<StockSearchResultDto> SearchStocks(String searchQuery);

}
