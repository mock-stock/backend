package stock.mock_stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stock.mock_stock.entity.SearchHistory;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    //  특정 사용자의 모든 검색 내역 조회 (최신순으로 정렬)
    List<SearchHistory> findByUserUidOrderByCreatedAtDesc(Long uid);

    // 특정 사용자의 최신 검색어 5개만 조회
    List<SearchHistory> findTop5ByUserUidOrderByCreatedAtDesc(Long uid);
}
