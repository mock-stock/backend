package stock.mock_stock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @Column(nullable = false)
    private String stockCode; // 주식 종목 코드

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType; // 매수(BUY) or 매도(SELL)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeActionType tradeAction; // 매수(BUY) or 매도(SELL)

    @Column(nullable = false)
    private Long stckOrdQty; // 주문 수량

    @Column(nullable = false)
    private Long stckOrdUnitPrice; // 주문 가격

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus; // NEW, PENDING, COMPLETED, CANCELED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();  // 엔티티가 수정될 때 변경 필요

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Order(User user, String stockCode, OrderType orderType, TradeActionType tradeAction, Long stckOrdQty, Long stckOrdUnitPrice, OrderStatus orderStatus) {
        this.user = user;
        this.stockCode = stockCode;
        this.orderType = orderType;
        this.tradeAction = tradeAction;
        this.stckOrdQty = stckOrdQty;
        this.stckOrdUnitPrice = stckOrdUnitPrice;
        this.orderStatus = orderStatus;
    }
}
