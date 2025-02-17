package stock.mock_stock.service;

import stock.mock_stock.dto.TestUserInfo;
import stock.mock_stock.dto.TokenInfo;

public interface LocalAuthService {
    public TokenInfo testAuthenticate(TestUserInfo testUserInfo);
}
