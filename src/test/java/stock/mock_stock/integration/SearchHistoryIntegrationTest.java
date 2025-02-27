package stock.mock_stock.integration;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import stock.mock_stock.dto.SearchHistoryProjection;
import stock.mock_stock.entity.SearchHistory;
import stock.mock_stock.entity.User;
import stock.mock_stock.repository.SearchHistoryRepository;
import stock.mock_stock.repository.UserRepository;
import stock.mock_stock.service.SearchHistoryService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.http.RequestEntity.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc  // MockMvc 사용 가능하게 설정
public class SearchHistoryIntegrationTest {

    @Autowired
    private SearchHistoryService searchHistoryService; // 서비스 계층


    // 레퍼지토리 계층
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @Autowired
    private MockMvc mockMvc;  // MockMvc를 @SpringBootTest에서 사용, 컨트롤러 계층까지 관여

    @BeforeEach
    void setUp(){
        testUser = userRepository.save(User.builder()
                .email("user@example.com")
                .nickname("nickname")
                .password("defaultPassword")
                .build());

       SearchHistory searchHistory=  searchHistoryRepository.save(new SearchHistory(testUser, "삼성"));
        System.out.println("searchHistory fid = " + searchHistory.getFid());
        searchHistoryRepository.save(new SearchHistory(testUser, "삼성"));
        searchHistoryRepository.save(new SearchHistory(testUser, "전자"));
    }

    @Test
    void getSearchHistory(){
        List<SearchHistoryProjection> result = searchHistoryService.getSearchHistory(testUser.getUid());

        assertEquals(2, result.size());
        assertEquals("전자", result.get(0).getSearch_keyword());
        assertEquals("삼성", result.get(1).getSearch_keyword());
    }

    @Test
    void deleteSearchHistory_Success(){
        // Given: 테스트 데이터를 미리 저장
        SearchHistory searchHistory = searchHistoryRepository.save(new SearchHistory(testUser, "삼성"));

        // When: 삭제 API 호출
        searchHistoryService.deleteSearchHistory(searchHistory.getFid(), testUser.getUid());

        // Then: 데이터가 삭제되었는지 확인
        assertFalse(searchHistoryRepository.findById(searchHistory.getFid()).isPresent());
    }

    @Test
    void deleteSearchHistory_Forbidden(){
        User anotherUser = userRepository.save(User.builder()
                .email("another@example.com")  // 다른 사용자
                .nickname("anotherUser")
                .password("password")
                .build());

        SearchHistory anotherUserHistory = searchHistoryRepository.save(
                new SearchHistory(anotherUser, "애플") // 다른 사용자의 검색 기록
        );

        // When & Then: 권한이 없는 사용자가 삭제 시 403 예외 발생
        assertThrows(AccessDeniedException.class, () -> {
            searchHistoryService.deleteSearchHistory(anotherUserHistory.getFid(), testUser.getUid());
        });
    }

    @Test
    void deleteSearchHistory_NotFound(){

        // When & Then: 권한이 없는 사용자가 삭제 시 401 예외 발생
        assertThrows(EntityNotFoundException.class, () -> {
            searchHistoryService.deleteSearchHistory(-1L, testUser.getUid());
        });
    }

    @Test
    void deleteSearchHistory_Unauthorized() throws Exception {
        // When & Then: 인증 없이 요청하면 401 발생
        mockMvc.perform(delete("/stocks/search/history/1")) // JWT 없이 요청
                .andExpect(status().isUnauthorized()); // 401 검증
    }
}
