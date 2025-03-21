package stock.mock_stock.service;

import stock.mock_stock.entity.OrderType;
import stock.mock_stock.entity.TradeActionType;

public interface OrderService {

    public void processOrder(Long userId, String stockCode, Long quantity, Long price, OrderType orderType, TradeActionType tradeActionType);

}
