package stock.mock_stock.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import stock.mock_stock.dto.SearchHistoryProjection;
import stock.mock_stock.entity.SearchHistory;
import stock.mock_stock.entity.User;
import stock.mock_stock.repository.SearchHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchHistoryServiceTest {

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @InjectMocks
    private SearchHistoryServiceImpl searchHistoryService;

    private User user;
    private SearchHistory searchHistory;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUid(1L);

        searchHistory = SearchHistory.builder()
                .user(user)
                .searchQuery("Test Query")
                .build();
    }

    @Test
    void saveSearchHistory() {
    }

    @Test
    void getSearchHistory() {
        // GIVEN
        SearchHistoryProjection mockProjection = mock(SearchHistoryProjection.class);

        when(mockProjection.getFid()).thenReturn(1L); // Mock객체의 특정 메서드 호출시 반환값 사전 정의
        when(mockProjection.getSearch_keyword()).thenReturn("Test Query"); // Mock객체의 특정 메서드 호출시 반환값 사전 정의

        List<SearchHistoryProjection> searchHistories = List.of(mockProjection); // searchHistories가 mockProject의 list 생성
        when(searchHistoryRepository.findByUserUidOrderByCreatedAtDescWithRowNumber(1L)).thenReturn(searchHistories); // 레퍼지토리 Mock 세팅

        // When
        List<SearchHistoryProjection> result = searchHistoryService.getSearchHistory(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Query", result.get(0).getSearch_keyword());
    }

    @Test
    void deleteSearchHistory() {
    }
}