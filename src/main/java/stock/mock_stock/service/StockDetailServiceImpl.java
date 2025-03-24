package stock.mock_stock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import stock.mock_stock.common.StockValidator;
import stock.mock_stock.dto.StockInfoDto;
import stock.mock_stock.dto.StockInfoOutput;
import stock.mock_stock.dto.StockKisHistoryDto;
import stock.mock_stock.entity.Stock;
import stock.mock_stock.repository.StockRepository;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class StockDetailServiceImpl implements StockDetailService {

    private final StockRepository stockRepository;
    private final KISApiService kisApiService;
    private final StockValidator stockValidator;
    private final Object apiLock = new Object(); // 동기화를 위한 Lock 객체
    @Override
    public StockInfoDto getStockInfo(String stckCode) {
        Stock stock = stockValidator.getStock(stckCode);

        StockInfoOutput result = kisApiService.getDomesticStockInfo("J", stckCode);
        // 결과 DTO 변환
        // TODO: 현시세, 종가대비차익 부분 임시 값 넣어놓고 추후 실제 데이터로 변경
        return StockInfoDto.builder()
                .sid(stock.getSid())
                .stckName(stock.getStckName())
                .stckCode(stock.getStckCode())
                .stckCurPrice(result.getStockKisDto().getStckPrpr())
                .stckPrevClsDiffPrice(result.getStockKisDto().getPrdyVrss())
                .stckPrevClsDiffPercent(result.getStockKisDto().getPrdyCtrt())
                .build();
    }


    @Override
    public List<StockKisHistoryDto> getStockHistory(String stckCode, LocalDate fromDate, LocalDate toDate, String interval) throws InterruptedException {
//        Stock stock = stockValidator.getStock(stckCode);
            return kisApiService.getStockHistoryInfo("J", stckCode, fromDate, toDate, interval);
    }
}
