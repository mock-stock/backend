package stock.mock_stock.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
public class Portfolio {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pfid;

    @ManyToOne
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @Column(nullable = false, length = 12)
    private String stckCode;

    @Column(nullable = false)
    private Long stckQty;

    @Column(nullable = false)
    private Long totalInitialAmount;

    @Column(nullable = false)
    private BigDecimal avgPurchasePrice;



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
