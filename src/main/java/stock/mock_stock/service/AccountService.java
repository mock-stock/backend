package stock.mock_stock.service;

import stock.mock_stock.dto.AccountResponseDto;

public interface AccountService {
    public AccountResponseDto getAccount(Long uid);
}
