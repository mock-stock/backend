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

    @OneToOne
    @JoinColumn(name = "uid")  // User를 직접 참조하지 않고 uid로 관리
    private User user;

    @Column(columnDefinition = "BIGINT DEFAULT 0")  // DB에서 기본값 0 설정
    private Long balance;


}
