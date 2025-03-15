package stock.mock_stock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import stock.mock_stock.common.StockValidator;
import stock.mock_stock.dto.StockInfoDto;
import stock.mock_stock.dto.StockInfoOutput;
import stock.mock_stock.dto.StockKisHistoryDto;
import stock.mock_stock.entity.Stock;
import stock.mock_stock.exception.InvalidStockCodeException;
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

        // TODO: 한투 API 호출하여 시세정보값 반환
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
        synchronized (apiLock){ // NOTE: 배치, 캐싱으로도 파라미터가 다를땐 처리할수없기떄문에 동기화로 단일 쓰레드로 요청사이에 0.5초를 주워 에러발생하지않도록 처리, 하지만 100명이 동시에 요청했다면 0.5*100 =50초 딜레이 발생
            // API 호출 전 대기 시간 추가 (기존 호출과 겹치지 않도록)
            Thread.sleep(500);
            return kisApiService.getStockHistoryInfo("J", stckCode, fromDate, toDate, interval);
        }

    }
}
