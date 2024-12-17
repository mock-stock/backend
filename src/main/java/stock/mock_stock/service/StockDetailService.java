package stock.mock_stock.service;

import stock.mock_stock.dto.StockInfoDto;

public interface StockDetailService {

    public StockInfoDto getStockInfo(String stckCode);
}
