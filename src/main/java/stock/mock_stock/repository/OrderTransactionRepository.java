package stock.mock_stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stock.mock_stock.entity.OrderTransaction;

public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {
}
