package stock.mock_stock.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import stock.mock_stock.entity.OrderStatus;
import stock.mock_stock.entity.OrderType;
import stock.mock_stock.entity.TradeActionType;

import java.time.LocalDateTime;

@Builder
@Data
public class OrderResponseDto {

    @NotNull
    private Long oid;

    @NotNull
    private String stckName;

    @NotNull
    private OrderType orderType; // 지정가(LIMIT) or 시장가(MARKET)

    @NotNull
    private TradeActionType tradeAction; // 매수(BUY) or 매도(SELL)

    @NotNull
    private Long stckOrdQty; // 주문 수량

    @NotNull
    private Long stckOrdUnitPrice; // 주문 가격

    @NotNull
    private OrderStatus orderStatus; // PENDING, FILLED, CANCELED

    @NotNull
    private LocalDateTime stckOrdTs; // 주문 요청 날짜



}
