package stock.mock_stock.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import stock.mock_stock.dto.AccountResponseDto;
import stock.mock_stock.entity.Account;
import stock.mock_stock.repository.AccountRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;

    @Override
    public AccountResponseDto getAccount(Long uid) {

        Account account = accountRepository.findByUserUid(uid)
                .orElseThrow(()-> new EntityNotFoundException("account with id "+ uid+ " not found"));

        if (!account.getUser().getUid().equals(uid)) {
            throw new AccessDeniedException("You are not authorized to get this account");
        }
        return AccountResponseDto.builder()
                .acid(account.getAcid())
                .balance(account.getBalance())
                .build();
    }
}
