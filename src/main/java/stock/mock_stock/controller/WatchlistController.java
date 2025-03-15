package stock.mock_stock.controller;

import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stock.mock_stock.dto.WatchlistResponseDto;
import stock.mock_stock.service.WatchlistService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        @DeleteMapping("/watchlist/{wid}")
        public ResponseEntity<?> deleteWatchlist(@PathVariable(value = "wid") Long wid){
            Map<String, String> response = new HashMap<>();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Claims claims) {
                    Long userId = Long.valueOf(claims.getSubject());
                    watchlistService.deleteWatchList(userId, wid);
                    return ResponseEntity.noContent().build();
                }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid");

        }
}
