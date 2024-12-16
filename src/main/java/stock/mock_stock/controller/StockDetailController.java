package stock.mock_stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import stock.mock_stock.dto.StockInfoDto;
import stock.mock_stock.exception.ErrorResult;
import stock.mock_stock.service.StockDetailService;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockDetailController {

    private final StockDetailService stockDetailService;

    @GetMapping("/{stockCode}")
    public StockInfoDto stockDetail(@PathVariable(value = "stockCode") String stockCode) {

        return stockDetailService.getStockInfo(stockCode);
    }
}
