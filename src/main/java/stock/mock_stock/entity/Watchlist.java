package stock.mock_stock.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Watchlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wid;

    @Column(name = "uid", nullable = false, insertable = false, updatable = false)
    private Long uid;  // User를 참조하지 않고 uid 값만 저장

    @Column(name = "sid", nullable = false)
    private Long sid;  // Stock을 참조하지 않고 sid 값만 저장

    @Column(name = "stck_code", nullable = false)
    private String stckCode;

    @ManyToOne(fetch = FetchType.LAZY)  // 🔥 User 엔티티와 ManyToOne 관계 추가
    @JoinColumn(name = "uid", nullable = false)  // uid 컬럼이 외래키 역할
    private User user;
}
