package stock.mock_stock.service;

import stock.mock_stock.dto.KakaoUserInfo;
import stock.mock_stock.dto.TokenInfo;

public interface KakaoAuthService {

    public TokenInfo authenticate(String accessToken); // TODO: String인지 아니면 AccessToken타입이 따로있는지 적용, void를 카카오에서 제공하는 방식으로 변경
    KakaoUserInfo getKakaoUserInfo(String accessToken);

}
