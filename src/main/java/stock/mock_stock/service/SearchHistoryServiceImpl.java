package stock.mock_stock.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    // TODO: 도메인 주도 설계 방식으로 수정 할것
    private final SearchHistoryRepository searchHistoryRepository;

    @Override
    public void saveSearchHistory(User user, String searchQuery) {
        SearchHistory searchHistory = SearchHistory.builder()
                .user(user)
                .searchQuery(searchQuery)
//                .createdAt(LocalDateTime.now())
                .build();

        searchHistoryRepository.save(searchHistory);
    }

    @Override
    public List<SearchHistoryProjection> getSearchHistory(Long userId) {

        return searchHistoryRepository.findByUserUidOrderByCreatedAtDescWithRowNumber(userId);
    }

    @Override
    public void deleteSearchHistory(Long fid, Long userId) {
        SearchHistory searchHistory = searchHistoryRepository.findById(fid)
                .orElseThrow(() -> new EntityNotFoundException("Search history with ID " + fid + " not found"));

        if (!searchHistory.getUser().getUid().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to delete this search history");
        }

        searchHistoryRepository.deleteById(fid);
    }
}
