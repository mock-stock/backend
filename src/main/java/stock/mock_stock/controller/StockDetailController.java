package stock.mock_stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import stock.mock_stock.dto.StockInfoDto;
import stock.mock_stock.dto.StockKisHistoryDto;
import stock.mock_stock.exception.ErrorResult;
import stock.mock_stock.service.StockDetailService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockDetailController {

    private final StockDetailService stockDetailService;

    @GetMapping("/{stockCode}")
    public StockInfoDto stockDetail(@PathVariable(value = "stockCode") String stockCode) {

        return stockDetailService.getStockInfo(stockCode);
    }

    @GetMapping("/history/{stockCode}")
    public List<StockKisHistoryDto> stockHistory(@PathVariable(value = "stockCode") String stockCode,
                                                 @RequestParam(value = "from_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                                                 @RequestParam(value = "to_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
                                                 @RequestParam(value = "interval") String interval){
    return stockDetailService.getStockHistory(stockCode, fromDate, toDate, interval);
    }
}
