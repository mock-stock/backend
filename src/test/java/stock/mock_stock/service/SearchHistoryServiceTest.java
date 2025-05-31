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
    void setUp(){

        user = new User();
        user.setUid(1L);

        searchHistory = SearchHistory.builder()
                .user(user)
                .searchQuery("Test Query")
                .build();
    }

    @Test
    void saveSearchHistory() {
        // NOTE: 목적 - SearchHistory 객체형식으로 save가 호출되고 성공했는가 주목적 테스트
        //given
        String query = searchHistory.getSearchQuery();
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
            void setUp(){

                user = new User();
                user.setUid(1L);

                searchHistory = SearchHistory.builder()
                        .user(user)
                        .searchQuery("Test Query")
                        .build();
            }

            @Test
            void saveSearchHistory() {
                // NOTE: 목적 - SearchHistory 객체형식으로 save가 호출되고 성공했는가 주목적 테스트
                //given
                String query = searchHistory.getSearchQuery();

                //when
                searchHistoryService.saveSearchHistory(user, query);

                //then
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
                // NOTE: 목적 - 검색히스토리 리스트 반환타입에 맞게 가져왔는지가 주목적, 반환타입 List<SearchHistoryProjection>
                // 목적을 이루기위해선 SearchHistoryProjection 값을 미리 정의해놓고, 그정의해논 값 토대로 findByUserUidOrderByCreatedAtDescWithRowNumber 요청시 List로 받도록한다
                // 그후 mock searchHistoryService를 통해 getHistorySearch 메서드 호출시 반환값을 result에 저장후
                // then 에서 각 값들을 확인

                // GIVEN
                SearchHistoryProjection mockProjection = mock(SearchHistoryProjection.class);

                when(mockProjection.getFid()).thenReturn(user.getUid()); // Mock객체의 특정 메서드 호출시 반환값 사전 정의
                when(mockProjection.getSearch_keyword()).thenReturn(searchHistory.getSearchQuery()); // Mock객체의 특정 메서드 호출시 반환값 사전 정의

                List<SearchHistoryProjection> searchHistories = List.of(mockProjection); // searchHistories가 mockProject의 list 생성
                when(searchHistoryRepository.findByUserUidOrderByCreatedAtDescWithRowNumber(1L)).thenReturn(searchHistories); // 레퍼지토리 Mock 세팅

                // When
                List<SearchHistoryProjection> result = searchHistoryService.getSearchHistory(1L);

                // Then
                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals("Test Query", result.get(0).getSearch_keyword());
                assertEquals(1L, result.get(0).getFid());
            }

            @Nested
            class deleteSearchHistory {
                // NOTE: 목적 - 성공, 실패1(해당 히스토리 fid가없을때), 실패2(해당 유저 권한이없을떄) 3가지 목적을 지닌 비즈니스로직 구현을 체크해야한다
                // 성공시에는 searchHistoryRepository값의 반환값이 있어야하므로 stub설정을해준다. 해당 fid를 잘찾은거니 해당 반환값인 SearchHistory를 만들어준다
                // 성공시 모든 조건 넘어가서 SearchHistoryRepository의 deleteById가 호출된것을 확인
                @Test
                void deleteSearchHistory_success(){
                    //given
                    Long fid = 1L;
                    Long userId = 100L;

                    User user = new User();
                    user.setUid(userId);

                    SearchHistory searchHistory = SearchHistory.builder()
                            .user(user)
                            .searchQuery("test")
                            .build();
                    when(searchHistoryRepository.findById(fid)).thenReturn(Optional.of(searchHistory));

                    //when
                    searchHistoryService.deleteSearchHistory(fid, userId);

                    //then
                    verify(searchHistoryRepository).deleteById(fid);

                }

                @Test
                void deleteSearchHistory_EntityNotFound_ThrowsException(){
                    //given
                    Long fid = 1L;
                    Long userId = 100L;

                    when(searchHistoryRepository.findById(fid)).thenReturn(Optional.empty());

                    //when, then
                    assertThrows(EntityNotFoundException.class, ()->
                            searchHistoryService.deleteSearchHistory(fid, userId));

                }

                @Test
                void deleteSearchHistory_AccessDenied_ThrowsException(){
                    //given

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

                    //when,then
                    assertThrows(AccessDeniedException.class, ()->
                            searchHistoryService.deleteSearchHistory(fid, userId));
                }

            }
        }
        //when
        searchHistoryService.saveSearchHistory(user, query);

        //then
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
        // NOTE: 목적 - 검색히스토리 리스트 반환타입에 맞게 가져왔는지가 주목적, 반환타입 List<SearchHistoryProjection>
        // 목적을 이루기위해선 SearchHistoryProjection 값을 미리 정의해놓고, 그정의해논 값 토대로 findByUserUidOrderByCreatedAtDescWithRowNumber 요청시 List로 받도록한다
        // 그후 mock searchHistoryService를 통해 getHistorySearch 메서드 호출시 반환값을 result에 저장후
        // then 에서 각 값들을 확인

        // GIVEN
        SearchHistoryProjection mockProjection = mock(SearchHistoryProjection.class);

        when(mockProjection.getFid()).thenReturn(user.getUid()); // Mock객체의 특정 메서드 호출시 반환값 사전 정의
        when(mockProjection.getSearch_keyword()).thenReturn(searchHistory.getSearchQuery()); // Mock객체의 특정 메서드 호출시 반환값 사전 정의

        List<SearchHistoryProjection> searchHistories = List.of(mockProjection); // searchHistories가 mockProject의 list 생성
        when(searchHistoryRepository.findByUserUidOrderByCreatedAtDescWithRowNumber(1L)).thenReturn(searchHistories); // 레퍼지토리 Mock 세팅

        // When
        List<SearchHistoryProjection> result = searchHistoryService.getSearchHistory(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Query", result.get(0).getSearch_keyword());
        assertEquals(1L, result.get(0).getFid());
    }

    @Nested
    class deleteSearchHistory {
        // NOTE: 목적 - 성공, 실패1(해당 히스토리 fid가없을때), 실패2(해당 유저 권한이없을떄) 3가지 목적을 지닌 비즈니스로직 구현을 체크해야한다
        // 성공시에는 searchHistoryRepository값의 반환값이 있어야하므로 stub설정을해준다. 해당 fid를 잘찾은거니 해당 반환값인 SearchHistory를 만들어준다
        // 성공시 모든 조건 넘어가서 SearchHistoryRepository의 deleteById가 호출된것을 확인
        @Test
        void deleteSearchHistory_success(){
            //given
            Long fid = 1L;
            Long userId = 100L;

            User user = new User();
            user.setUid(userId);

            SearchHistory searchHistory = SearchHistory.builder()
                    .user(user)
                    .searchQuery("test")
                    .build();
            when(searchHistoryRepository.findById(fid)).thenReturn(Optional.of(searchHistory));

            //when
            searchHistoryService.deleteSearchHistory(fid, userId);

            //then
            verify(searchHistoryRepository).deleteById(fid);

        }

        @Test
        void deleteSearchHistory_EntityNotFound_ThrowsException(){
            //given
            Long fid = 1L;
            Long userId = 100L;

            when(searchHistoryRepository.findById(fid)).thenReturn(Optional.empty());

            //when, then
            assertThrows(EntityNotFoundException.class, ()->
                    searchHistoryService.deleteSearchHistory(fid, userId));

        }

        @Test
        void deleteSearchHistory_AccessDenied_ThrowsException(){
            //given

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

            //when,then
            assertThrows(AccessDeniedException.class, ()->
                    searchHistoryService.deleteSearchHistory(fid, userId));
        }

    }
}