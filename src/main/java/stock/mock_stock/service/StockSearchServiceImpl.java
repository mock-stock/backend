package stock.mock_stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stock.mock_stock.dto.StockSearchResultDto;
import stock.mock_stock.entity.SearchHistory;
import stock.mock_stock.entity.Stock;
import stock.mock_stock.entity.User;
import stock.mock_stock.repository.SearchHistoryRepository;
import stock.mock_stock.repository.StockRepository;
import stock.mock_stock.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockSearchServiceImpl implements StockSearchService{

    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final SearchHistoryService searchHistoryService;
    @Override
    public List<StockSearchResultDto> searchStocks(String searchQuery) {
        // 레포지토리에서 검색 수행
        List<Stock> stocks = stockRepository.findAllByQuery(searchQuery);

//        System.out.println("stocks = " + stocks);
        // 검색 결과를 DTO로 변환
        return stocks.stream().map(stock -> {
            StockSearchResultDto resultDto = new StockSearchResultDto(stock.getSid(),stock.getStckName(),stock.getStckCode());
            return resultDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StockSearchResultDto> searchStocksWithHistory(String searchQuery, Long userId) {

        Optional<User> user = userRepository.findById(userId);
        user.ifPresent(u -> searchHistoryService.saveSearchHistory(u, searchQuery));  // 검색 기록 저장

        return searchStocks(searchQuery);
    }


}
