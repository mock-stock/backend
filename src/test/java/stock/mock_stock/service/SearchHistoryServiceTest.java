package stock.mock_stock.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import stock.mock_stock.dto.SearchHistoryProjection;
import stock.mock_stock.entity.SearchHistory;
import stock.mock_stock.entity.User;
import stock.mock_stock.repository.SearchHistoryRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchHistoryServiceTest {

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @InjectMocks
    private SearchHistoryServiceImpl searchHistoryService;

    private User user;
    private SearchHistory searchHistory;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUid(1L);

        searchHistory = SearchHistory.builder()
                .user(user)
                .searchQuery("Test Query")
                .build();
    }

    @Test
    void saveSearchHistory() {
        // given
        String query = searchHistory.getSearchQuery();

        // when
        searchHistoryService.saveSearchHistory(user, query);

        // then
        verify(searchHistoryRepository).save(
                argThat(history ->
                        history.getUser().equals(user) &&
                                history.getSearchQuery().equals(query)
                )
        );
        verify(searchHistoryRepository, times(1)).save(any(SearchHistory.class));
    }

    @Test
    void getSearchHistory() {
        // given
        SearchHistoryProjection mockProjection = mock(SearchHistoryProjection.class);
        when(mockProjection.getFid()).thenReturn(user.getUid());
        when(mockProjection.getSearch_keyword()).thenReturn(searchHistory.getSearchQuery());

        List<SearchHistoryProjection> searchHistories = List.of(mockProjection);
        when(searchHistoryRepository.findByUserUidOrderByCreatedAtDescWithRowNumber(1L)).thenReturn(searchHistories);

        // when
        List<SearchHistoryProjection> result = searchHistoryService.getSearchHistory(1L);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Query", result.get(0).getSearch_keyword());
        assertEquals(1L, result.get(0).getFid());
    }

    @Nested
    class DeleteSearchHistory {

        @Test
        void deleteSearchHistory_success() {
            // given
            Long fid = 1L;
            Long userId = 100L;

            User user = new User();
            user.setUid(userId);

            SearchHistory searchHistory = SearchHistory.builder()
                    .user(user)
                    .searchQuery("test")
                    .build();

            when(searchHistoryRepository.findById(fid)).thenReturn(Optional.of(searchHistory));

            // when
            searchHistoryService.deleteSearchHistory(fid, userId);

            // then
            verify(searchHistoryRepository).deleteById(fid);
        }

        @Test
        void deleteSearchHistory_EntityNotFound_ThrowsException() {
            // given
            Long fid = 1L;
            Long userId = 100L;

            when(searchHistoryRepository.findById(fid)).thenReturn(Optional.empty());

            // when & then
            assertThrows(EntityNotFoundException.class, () ->
                    searchHistoryService.deleteSearchHistory(fid, userId));
        }

        @Test
        void deleteSearchHistory_AccessDenied_ThrowsException() {
            // given
            Long fid = 1L;
            Long userId = 100L;
            Long anotherUserId = 200L;

            User anotherUser = new User();
            anotherUser.setUid(anotherUserId);

            SearchHistory searchHistory = SearchHistory.builder()
                    .user(anotherUser)
                    .searchQuery("other")
                    .build();

            when(searchHistoryRepository.findById(fid)).thenReturn(Optional.of(searchHistory));

            // when & then
            assertThrows(AccessDeniedException.class, () ->
                    searchHistoryService.deleteSearchHistory(fid, userId));
        }
    }
}