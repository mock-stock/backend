package stock.mock_stock.service;

import stock.mock_stock.dto.StockInfoDto;
import stock.mock_stock.dto.StockKisHistoryDto;

import java.time.LocalDate;
import java.util.List;

public interface StockDetailService {

    public StockInfoDto getStockInfo(String stckCode);
    public List<StockKisHistoryDto> getStockHistory(String stckCode, LocalDate fromDate, LocalDate toDate, String interval) throws InterruptedException;
}
