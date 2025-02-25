package stock.mock_stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stock.mock_stock.dto.SearchHistoryProjection;
import stock.mock_stock.entity.SearchHistory;
import stock.mock_stock.entity.User;
import stock.mock_stock.repository.SearchHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryServiceImpl implements SearchHistoryService {
    private final SearchHistoryRepository searchHistoryRepository;

    @Override
    public void saveSearchHistory(User user, String searchQuery) {
        SearchHistory searchHistory = SearchHistory.builder()
                .user(user)
                .searchQuery(searchQuery)
                .createdAt(LocalDateTime.now())
                .build();

        searchHistoryRepository.save(searchHistory);
    }

    @Override
    public List<SearchHistoryProjection> getSearchHistory(Long userId) {

        return searchHistoryRepository.findByUserUidOrderByCreatedAtDescWithRowNumber(userId);
    }
}
