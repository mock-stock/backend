package stock.mock_stock.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stock.mock_stock.dto.StockSearchResultDto;
import stock.mock_stock.entity.User;
import stock.mock_stock.service.StockSearchService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockSearchController {

    private final StockSearchService stockSearchService;


    @GetMapping("/search/{searchQuery}")
    public List<StockSearchResultDto> searchStocks(@PathVariable(value = "searchQuery") String searchQuery){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("SecurityContextHolder authentication = " + authentication);
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Claims claims) {
            Long userId = Long.valueOf(claims.getSubject());
            return stockSearchService.searchStocksWithHistory(searchQuery, userId);  // userId만 전달
        } else{
        return stockSearchService.searchStocks(searchQuery);
        }
    }

}
