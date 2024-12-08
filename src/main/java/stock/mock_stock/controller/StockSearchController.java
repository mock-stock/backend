package stock.mock_stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stock.mock_stock.dto.StockSearchResultDto;
import stock.mock_stock.service.StockSearchService;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockSearchController {

    private final StockSearchService stockSearchService;


    @GetMapping("/search/{searchQuery}")
    public List<StockSearchResultDto> searchStocks(@PathVariable(value = "searchQuery") String searchQuery){
        return stockSearchService.searchStocks(searchQuery);
    }
}
