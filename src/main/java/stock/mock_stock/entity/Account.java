package stock.mock_stock.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long acid;

    @Column(name = "uid", nullable = false, unique = true) // 외래키 직접 관리
    private Long uid;

    private Long balance;

    @OneToOne
    @JoinColumn(name = "uid", insertable = false, updatable = false)  // User를 직접 참조하지 않고 uid로 관리
    private User user;
}
