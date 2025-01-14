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
import stock.mock_stock.exception.NotFoundStockException;
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
    public List<StockKisHistoryDto> getStockHistory(String stckCode, LocalDate fromDate, LocalDate toDate, String interval) {
        Stock stock = stockValidator.getStock(stckCode);

        StockInfoOutput result = kisApiService.getStockHistoryInfo("J", stckCode, fromDate, toDate, interval);

        return result.getStockKisHistoryDto().stream().peek(dto -> {
            dto.setStckName(stock.getStckName());
            // NOTE: 공식 : 전일대비액(오늘종가 - 어제종가) / 어제종가 * 100 = prdy_vrss(종가차액)/stck_clpr - prdy_vrss(현재종가 - 종가대비 = 어제종가), 어제종가액을 주지않기에 이런식으로 공식만들어 사용
            Double changedRate = ((double)dto.getPrdyVrss()/(double)(dto.getStckClpr() - dto.getPrdyVrss())) * 100;
            dto.setStckChangeRate(Math.round(changedRate*100)/100.0); // 두자리수만 표시하기위해
        }).toList();
    }
}
