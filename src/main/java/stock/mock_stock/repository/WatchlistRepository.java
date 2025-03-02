package stock.mock_stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stock.mock_stock.entity.Watchlist;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUserUid(Long uid);
    boolean existsByUserUidAndSid(Long uid, Long sid);

}
