package stock.mock_stock.service;

import stock.mock_stock.dto.OAuthToken;
import stock.mock_stock.dto.StockInfoOutput;

public interface KISApiService {

    public StockInfoOutput getDomesticStockInfo(String fidCondMrktDivCode, String fidInputIscd);
    public OAuthToken fetchOauthToken(String grantType, String appKey, String appSecret);
    public OAuthToken getTokenInfo(String key);
    public void saveTokenInfo(String key, OAuthToken oAuthToken);
    public boolean checkTokenAvailable(String key);
    public void startKISWebsocket();
    public String fetchWebApprovalKey(String grantType, String appKey, String appSecret);

}
