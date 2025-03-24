package stock.mock_stock.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import stock.mock_stock.dto.PortfolioResponseDto;
import stock.mock_stock.service.PortfolioService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;
    @GetMapping("/portfolio")
    public List<PortfolioResponseDto> getUserPortfolios(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Claims claims) {
            Long userId = Long.valueOf(claims.getSubject());

            return portfolioService.getPortfolioWithEvaluation(userId);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated"); // 401 반환
    }
}
