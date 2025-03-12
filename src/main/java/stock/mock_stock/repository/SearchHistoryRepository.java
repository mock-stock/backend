package stock.mock_stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stock.mock_stock.dto.SearchHistoryProjection;
import stock.mock_stock.entity.SearchHistory;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    //  특정 사용자의 모든 검색 내역 조회 (최신순으로 정렬)
    // TODO: 이부분 with 부분이랑 join user부분 필요없을시 제거하고 가져오도록 수정
    @Query(value = """ 
    WITH LatestSearch AS (
        SELECT 
            sh.fid, 
            sh.search_query AS search_keyword,
            ROW_NUMBER() OVER (PARTITION BY sh.search_query ORDER BY sh.created_at DESC) AS rn
        FROM search_history sh
        JOIN ms_user u ON sh.uid = u.uid
        WHERE u.uid = :uid
    )
    SELECT 
        sh.fid, 
        sh.search_keyword 
    FROM LatestSearch sh
    WHERE sh.rn = 1
    ORDER BY sh.fid DESC
    """, nativeQuery = true)
    List<SearchHistoryProjection> findByUserUidOrderByCreatedAtDescWithRowNumber(@Param("uid") Long uid); // TODO: 추후 jpql로 못바꾸는지 고려필요


    // 특정 사용자의 최신 검색어 5개만 조회
    List<SearchHistory> findTop5ByUserUidOrderByCreatedAtDesc(Long uid);
}
