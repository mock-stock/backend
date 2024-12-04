package stock.mock_stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stock.mock_stock.entity.Stock;

import java.util.List;

public interface StockSearchRepository extends JpaRepository<Stock, Long> {

    // 1. 이름 또는 코드로 검색 (간단한 경우)
    @Query("SELECT s FROM Stock s WHERE s.stckName LIKE %:searchQuery% OR s.stckCode = :searchQuery")
    List<Stock> findByQuery(@Param("searchQuery") String searchQuery);


//    // 2. Watched 상태를 필터링할 경우 추가, 아직 필터필요없음
//    @Query("SELECT s FROM Stock s WHERE (s.stckName LIKE %:searchQuery% OR s.stckCode = :searchQuery) AND s.isWatched = :isWatched")
//    List<Stock> findByKeywordAndWatched(@Param("searchQuery") String searchQuery, @Param("isWatched") Boolean isWatched);

}



