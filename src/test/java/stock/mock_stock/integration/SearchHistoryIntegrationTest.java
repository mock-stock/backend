package stock.mock_stock.integration;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import stock.mock_stock.dto.SearchHistoryProjection;
import stock.mock_stock.entity.SearchHistory;
import stock.mock_stock.entity.User;
import stock.mock_stock.repository.SearchHistoryRepository;
import stock.mock_stock.repository.UserRepository;
import stock.mock_stock.service.SearchHistoryService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class SearchHistoryIntegrationTest {

    @Autowired
    private SearchHistoryService searchHistoryService;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp(){
        testUser = userRepository.save(User.builder()
                .email("user@example.com")
                .nickname("nickname")
                .password("defaultPassword")
                .build());


        searchHistoryRepository.save(new SearchHistory(testUser, "삼성", null));
        searchHistoryRepository.save(new SearchHistory(testUser, "삼성", null));
        searchHistoryRepository.save(new SearchHistory(testUser, "전자", null));
    }

    @Test
    void testGetSearchHistory(){
        List<SearchHistoryProjection> result = searchHistoryService.getSearchHistory(testUser.getUid());

        assertEquals(2, result.size());
        assertEquals("전자", result.get(0).getSearch_keyword());
        assertEquals("삼성", result.get(1).getSearch_keyword());
    }
}
