package stock.mock_stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stock.mock_stock.dto.StockNameProjection;
import stock.mock_stock.entity.Stock;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("SELECT s FROM Stock s")
    List<Stock> findAllStocks();

    @Query("SELECT s.sid AS sid, s.stckCode AS stckCode, s.stckName AS stckName FROM Stock s")
    List<StockNameProjection> findAllStockNames();

    // 1. 이름 또는 코드로 검색 (간단한 경우)
    @Query("SELECT s FROM Stock s WHERE s.stckName LIKE CONCAT('%', :searchQuery, '%') OR s.stckCode = :searchQuery")
    List<Stock> findAllByQuery(@Param("searchQuery") String searchQuery);


//    // 2. Watched 상태를 필터링할 경우 추가, 아직 필터필요없음
//    @Query("SELECT s FROM Stock s WHERE (s.stckName LIKE %:searchQuery% OR s.stckCode = :searchQuery) AND s.isWatched = :isWatched")
//    List<Stock> findByKeywordAndWatched(@Param("searchQuery") String searchQuery, @Param("isWatched") Boolean isWatched);

    // 주식 단일 정보
    @Query("SELECT s FROM Stock s WHERE s.stckCode = :stckCode")
    Stock findByStockCode(@Param("stckCode") String stckCode);
}



