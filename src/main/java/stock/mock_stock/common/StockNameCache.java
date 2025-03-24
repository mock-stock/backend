package stock.mock_stock.common;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import stock.mock_stock.dto.StockBasicInfoDto;
import stock.mock_stock.dto.StockNameProjection;
import stock.mock_stock.repository.StockRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockNameCache {

    private final StockRepository stockRepository;
    private final Map<String, StockBasicInfoDto> stockNames = new ConcurrentHashMap<>();


    // 캐시에서 종목 이름 가져오기
    public StockBasicInfoDto getStockNameAndSid(String stockCode) {
        return stockNames.get(stockCode);
    }

    // 초기화 시 DB에서 모든 종목 정보 로드
    @PostConstruct
    public void loadAllStockNamesFromDB() {
        Map<String, StockBasicInfoDto> allStockNames = stockRepository.findAllStockNames().stream()
                .collect(Collectors.toMap(
                        StockNameProjection::getStckCode,
                        projection -> StockBasicInfoDto.builder()
                                .sid(projection.getSid())
                                .stckName(projection.getStckName())
                                .stckCode(projection.getStckCode())
                                .build()
                ));
        stockNames.putAll(allStockNames);
    }


}
