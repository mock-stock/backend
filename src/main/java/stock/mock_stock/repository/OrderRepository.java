package stock.mock_stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stock.mock_stock.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
