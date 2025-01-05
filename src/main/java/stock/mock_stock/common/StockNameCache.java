package stock.mock_stock.common;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import stock.mock_stock.dto.StockNameProjection;
import stock.mock_stock.repository.StockRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockNameCache {

    private final StockRepository stockRepository;
    private final Map<String, String> stockNames = new ConcurrentHashMap<>();


    // 캐시에서 종목 이름 가져오기
    public String getStockName(String stockCode) {
        return stockNames.get(stockCode);
    }

    // 초기화 시 DB에서 모든 종목 정보 로드
    @PostConstruct
    public void loadAllStockNamesFromDB() {
        System.out.println("Loading stock names from database...");

        // DB에서 StockNameProjection을 가져와서 Map으로 변환
        Map<String, String> allStockNames = stockRepository.findAllStockNames().stream()
                .collect(Collectors.toMap(StockNameProjection::getStckCode, StockNameProjection::getStckName));
        System.out.println("allStockNames = " + allStockNames);
        stockNames.putAll(allStockNames);
        System.out.println("Stock names loaded: " + stockNames);

    }


}
