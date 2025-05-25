package stock.mock_stock.service;

import stock.mock_stock.dto.OrderResponseDto;
import stock.mock_stock.entity.OrderStatus;
import stock.mock_stock.entity.OrderType;
import stock.mock_stock.entity.TradeActionType;

import java.util.List;

public interface OrderService {

    public void processOrder(Long userId, String stockCode, Long quantity, Long price, OrderType orderType, TradeActionType tradeActionType);
    public List<OrderResponseDto> getOrders(Long uid,String stockCode, OrderStatus status);
}
