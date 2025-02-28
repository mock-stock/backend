package stock.mock_stock.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Watchlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wid;

    @Column(name = "uid", nullable = false)
    private Long uid;  // User를 참조하지 않고 uid 값만 저장

    @Column(name = "sid", nullable = false)
    private Long sid;  // Stock을 참조하지 않고 sid 값만 저장

    @Column(name = "stck_code", nullable = false)
    private String stckCode;
}
