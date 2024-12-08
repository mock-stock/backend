package stock.mock_stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import stock.mock_stock.dto.StockSearchResultDto;
import stock.mock_stock.entity.Stock;
import stock.mock_stock.repository.StockSearchRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockSearchServiceImpl implements StockSearchService{

    private final StockSearchRepository stockSearchRepository;


    @Override
    public List<StockSearchResultDto> searchStocks(String searchQuery) {
        // 레포지토리에서 검색 수행
        List<Stock> stocks = stockSearchRepository.findAllByQuery(searchQuery);
        System.out.println("stocks = " + stocks);
        // 검색 결과를 DTO로 변환
        return stocks.stream().map(stock -> {
            StockSearchResultDto resultDto = new StockSearchResultDto(stock.getSid(),stock.getStckName(),stock.getStckCode() , null ); // TODO: 추후 isWatched 연동하는거 찾아볼것
            return resultDto;
        }).collect(Collectors.toList());
    }
}
