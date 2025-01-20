package stock.mock_stock.service;

import stock.mock_stock.dto.OAuthToken;
import stock.mock_stock.dto.StockInfoOutput;
import stock.mock_stock.dto.StockKisHistoryDto;

import java.time.LocalDate;
import java.util.List;

public interface KISApiService {

    public StockInfoOutput getDomesticStockInfo(String fidCondMrktDivCode, String fidInputIscd);
    public List<StockKisHistoryDto> getStockHistoryInfo(String fidCondMrktDivCode, String fidInputIscd, LocalDate fidInputDate1, LocalDate fidInputDate2, String interval);
    public OAuthToken fetchOauthToken(String grantType, String appKey, String appSecret);
    public OAuthToken getTokenInfo(String key);
    public void saveTokenInfo(String key, OAuthToken oAuthToken);
    public boolean checkTokenAvailable(String key);
    public void startKISWebsocket();
    public String fetchWebApprovalKey(String grantType, String appKey, String appSecret);

}
