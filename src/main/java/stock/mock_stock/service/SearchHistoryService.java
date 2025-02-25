package stock.mock_stock.service;

import stock.mock_stock.dto.SearchHistoryProjection;
import stock.mock_stock.entity.User;

import java.util.List;

public interface SearchHistoryService {
    public void saveSearchHistory(User user, String searchQuery);
    public List<SearchHistoryProjection> getSearchHistory(Long userId);
}
