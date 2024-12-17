package stock.mock_stock.common;

import org.springframework.stereotype.Component;
import stock.mock_stock.dto.OAuthToken;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStorage {
    private final ConcurrentHashMap<String, OAuthToken> tokenMap = new ConcurrentHashMap<>();

    // 토큰 저장 (key와 만료시간 포함)
    public void saveToken(String key, OAuthToken oAuthToken) {
        tokenMap.put(key, oAuthToken);
    }

    // 토큰 조회
    public OAuthToken getToken(String key) {
        OAuthToken tokenInfo = tokenMap.get(key);
        return tokenInfo;
    }

    // 토큰 존재 여부 확인 (만료 여부 포함)
    public boolean isTokenAvailable(String key) {
        OAuthToken tokenInfo = tokenMap.get(key);
        return tokenInfo != null && !tokenInfo.isExpired();
    }

    // 토큰 제거
    public void removeToken(String key) {
        tokenMap.remove(key);
    }
}
