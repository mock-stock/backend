package stock.mock_stock.service;

import stock.mock_stock.entity.SearchHistory;
import stock.mock_stock.entity.User;

import java.util.List;

public interface SearchHistoryService {
    public void saveSearchHistory(User user, String searchQuery);
    public List<SearchHistory> getSearchHistory(Long userId);
}
