package stock.mock_stock.controller;

import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stock.mock_stock.dto.SearchHistoryProjection;
import stock.mock_stock.dto.StockSearchResultDto;
import stock.mock_stock.service.SearchHistoryService;
import stock.mock_stock.service.StockSearchService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockSearchController {

    private final StockSearchService stockSearchService;
    private final SearchHistoryService searchHistoryService;


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

    @GetMapping("/search/history")
    public List<SearchHistoryProjection> searchHistory(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Claims claims) {
            Long userId = Long.valueOf(claims.getSubject());
        return searchHistoryService.getSearchHistory(userId);
        }
        return null;
    }

    @DeleteMapping("/search/history/{fid}")
    public ResponseEntity<Object> deleteSearchHistory(@PathVariable(value = "fid") Long fid){
        Map<String, String> response = new HashMap<>();

        try{
        searchHistoryService.deleteSearchHistory(fid);
        return ResponseEntity.noContent().build();
        }catch (EntityNotFoundException e){
            response.put("message", e.getMessage()); // ✅ 직접 宣言
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        }

}
