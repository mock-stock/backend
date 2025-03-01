package stock.mock_stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stock.mock_stock.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
