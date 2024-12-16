package stock.mock_stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stock.mock_stock.dto.StockInfoDto;
import stock.mock_stock.entity.Stock;
import stock.mock_stock.repository.StockRepository;

@Service
@RequiredArgsConstructor
public class StockDetailServiceImpl implements StockDetailService {

    private final StockRepository stockRepository;

    @Override
    public StockInfoDto getStockInfo(String stckCode) {
        Stock stock = stockRepository.findByStockCode(stckCode);
        System.out.println("stock = " + stock);
        // TODO: 한투 API 호출하여 시세정보값 반환

        // 결과 DTO 변환
        // TODO: 현시세, 종가대비차익 부분 임시 값 넣어놓고 추후 실제 데이터로 변경
        StockInfoDto stockInfoDto = new StockInfoDto(
                stock.getSid(),
                stock.getStckName(),
                stock.getStckCode(),
                999L,
                99L,
                99.99 );
        return stockInfoDto;
    }
}
