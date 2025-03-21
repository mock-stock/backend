package stock.mock_stock.service;

import stock.mock_stock.dto.AccountResponseDto;
import stock.mock_stock.entity.Account;
import stock.mock_stock.entity.TransactionType;

public interface AccountService {
    public AccountResponseDto getAccount(Long uid);
    public void processTransaction(Long uid, Long amount);
    public void recordTransaction(Account account, Long amount, TransactionType type);
}
