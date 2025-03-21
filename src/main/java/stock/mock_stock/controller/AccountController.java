package stock.mock_stock.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import stock.mock_stock.dto.AccountResponseDto;
import stock.mock_stock.dto.TransactionRequestDto;
import stock.mock_stock.entity.TransactionType;
import stock.mock_stock.service.AccountService;


@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/account")
    public AccountResponseDto getAccount(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Claims claims) {
            Long userId = Long.valueOf(claims.getSubject());
            return accountService.getAccount(userId);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated"); // 401 반환
    }

    @PostMapping("/transaction")
    public void processTransaction(@RequestBody TransactionRequestDto transactionRequestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Claims claims) {
            Long userId = Long.valueOf(claims.getSubject());
            accountService.processTransaction(userId, transactionRequestDto.getAmount());
            return;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated"); // 401 반환
    }
}
