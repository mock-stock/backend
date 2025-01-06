package stock.mock_stock.common;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class KISSubscritionManager { // TODO: 아직 미사용중, KIS 중복 구독 관리를위해 만든건데 어차피 ALREADY SUBSCRIBED 이런 메세지오니깐 필요한지 검토필요

    private final Set<String> subscribedStocks = ConcurrentHashMap.newKeySet();

    public boolean isSubscribed(String stockCode) {
        return subscribedStocks.contains(stockCode);
    }

    public void addSubscription(String stockCode) {
        subscribedStocks.add(stockCode);
    }
}
