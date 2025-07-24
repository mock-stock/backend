package stock.mock_stock.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stock.mock_stock.dto.AccountResponseDto;
import stock.mock_stock.entity.Account;
import stock.mock_stock.entity.AccountTransaction;
import stock.mock_stock.entity.TransactionType;
import stock.mock_stock.repository.AccountRepository;
import stock.mock_stock.repository.TransitionRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
    // TODO: 도메인 주도 설계 방식으로 수정 할것
    private final AccountRepository accountRepository;
    private final TransitionRepository transitionRepository;

    @Override
    public AccountResponseDto getAccount(Long uid) {

        Account account = accountRepository.findWithUserByUid(uid)
                .orElseThrow(()-> new EntityNotFoundException("account with id "+ uid+ " not found"));

        if (!account.getUser().getUid().equals(uid)) {
            throw new AccessDeniedException("You are not authorized to get this account");
        }
        return AccountResponseDto.builder()
                .acid(account.getAcid())
                .balance(account.getBalance())
                .build();
    }

    @Override
    @Transactional
    public void processTransaction(Long uid, Long amount) {
        Account account = accountRepository.findByUserUid(uid)
                .orElseThrow(()-> new EntityNotFoundException("account with id "+ uid+ " not found"));

        if (!account.getUser().getUid().equals(uid)) {
            throw new AccessDeniedException("You are not authorized to get this account");
        }

        TransactionType transactionType = amount < 0 ? TransactionType.WITHDRAWAL : TransactionType.DEPOSIT;

        if (transactionType == TransactionType.WITHDRAWAL && account.getBalance() + amount < 0) {
            throw new IllegalArgumentException("Insufficient balance"); // 예외 발생
        }

        AccountTransaction accountTransaction = AccountTransaction.builder()
                .account(account)
                .amount(amount)
                .transactionType(transactionType)
                .build();

        //계좌 내역 추가
        transitionRepository.save(accountTransaction);

        //잔액 업데이트
        account.setBalance(account.getBalance() + amount);
//        accountRepository.save(account); // NOTE: Dirty Checking wjrdyd

    }

    @Override
    public void recordTransaction(Account account, Long amount, TransactionType type) {
        AccountTransaction accountTransaction = AccountTransaction.builder()
                .account(account)
                .amount(amount)
                .transactionType(type)
                .build();
        transitionRepository.save(accountTransaction);
    }
}
