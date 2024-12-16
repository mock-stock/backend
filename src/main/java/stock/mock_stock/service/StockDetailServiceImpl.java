package stock.mock_stock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import stock.mock_stock.dto.StockInfoDto;
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
        String result = kisApiService.getDomesticStockInfo("J", stckCode);
        // JSON 문자열을 JSONObject로 변환
        JSONObject jsonObject = new JSONObject(result);
        // output 이라는 객체가 또 있기때문에 Json으로 다시빼고 string으로 변환
        String output = jsonObject.getJSONObject("output").toString();
        ObjectMapper objectMapper = new ObjectMapper();
        StockKisDto stockKisDto;
        try {
            // StockDto 클래스의 속성으로 값들 매핑
            stockKisDto = objectMapper.readValue(output, StockKisDto.class);
            System.out.println("result = " +stockKisDto.getStckPrpr());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 결과 DTO 변환
        // TODO: 현시세, 종가대비차익 부분 임시 값 넣어놓고 추후 실제 데이터로 변경
        StockInfoDto stockInfoDto = new StockInfoDto(
                stock.getSid(),
                stock.getStckName(),
                stock.getStckCode(),
                stockKisDto.getStckPrpr(),
                stockKisDto.getPrdyVrss(),
                stockKisDto.getPrdyCtrt() );
        return stockInfoDto;
    }
}
