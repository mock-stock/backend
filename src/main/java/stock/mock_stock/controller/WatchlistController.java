package stock.mock_stock.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stock.mock_stock.dto.WatchlistResponseDto;
import stock.mock_stock.service.WatchlistService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class WatchlistController {
    private final WatchlistService watchlistService;
    @GetMapping("/watchlist")
    public List<WatchlistResponseDto> getWatchlist(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Claims claims) {
            Long userId = Long.valueOf(claims.getSubject());
            return watchlistService.getWatchList(userId);
        }
        return Collections.emptyList();
        }

        @PostMapping("/watchlist/{sid}")
        public void addWatchlist(@PathVariable(value = "sid") Long sid){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Claims claims) {
                Long userId = Long.valueOf(claims.getSubject());
                 watchlistService.addWatchlist(userId, sid);
            }
        }
}
