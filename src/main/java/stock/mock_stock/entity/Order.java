package stock.mock_stock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
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
    private String stckCode; // 주식 종목 코드

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType; // 지정가(LIMIT) or 시장가(MARKET)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeActionType tradeAction; // 매수(BUY) or 매도(SELL)

    @Column(nullable = false)
    private Long stckOrdQty; // 주문 수량

    @Column(nullable = false)
    private Long stckOrdUnitPrice; // 주문 가격

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus; // PENDING, FILLED, CANCELED

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

}
