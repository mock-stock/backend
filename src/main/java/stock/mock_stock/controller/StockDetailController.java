package stock.mock_stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stock.mock_stock.dto.StockInfoDto;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockDetailController {

    @GetMapping("/{stockCode}")
    public StockInfoDto stockDetail(@PathVariable(value = "stockCode") String stockCode) {
        // TODO: 주식시세정보 레포지토리 추가
        return null;
    }
}
