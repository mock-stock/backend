package stock.mock_stock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import stock.mock_stock.dto.StockInfoDto;
import stock.mock_stock.dto.StockInfoOutput;
import stock.mock_stock.dto.StockKisDto;
import stock.mock_stock.entity.Stock;
import stock.mock_stock.exception.InvalidStockCodeException;
import stock.mock_stock.exception.NotFoundStockException;
import stock.mock_stock.repository.StockRepository;


@Slf4j
@Service
@RequiredArgsConstructor
public class StockDetailServiceImpl implements StockDetailService {

    private final StockRepository stockRepository;
    private final KISApiService kisApiService;
    @Override
    public StockInfoDto getStockInfo(String stckCode) {
        if(stckCode.length() != 6) throw new InvalidStockCodeException("Invalid stock code : " + stckCode);
        Stock stock = stockRepository.findByStockCode(stckCode);
        System.out.println("stock = " + stock);
        if(stock == null) throw new NotFoundStockException("Not Found stock code: " + stckCode);

        // TODO: 한투 API 호출하여 시세정보값 반환
        StockInfoOutput result = kisApiService.getDomesticStockInfo("J", stckCode);
        // 결과 DTO 변환
        // TODO: 현시세, 종가대비차익 부분 임시 값 넣어놓고 추후 실제 데이터로 변경
        StockInfoDto stockInfoDto = new StockInfoDto(
                stock.getSid(),
                stock.getStckName(),
                stock.getStckCode(),
                result.getStockKisDto().getStckPrpr(),
                result.getStockKisDto().getPrdyVrss(),
                result.getStockKisDto().getPrdyCtrt() );
        return stockInfoDto;
    }
}
