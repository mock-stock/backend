package stock.mock_stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stock.mock_stock.entity.Portfolio;
import stock.mock_stock.entity.User;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    public Optional<Portfolio> findByUserAndStckCode(User user, String stckCode);
}
