package stock.mock_stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stock.mock_stock.dto.StockInfoDto;
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
