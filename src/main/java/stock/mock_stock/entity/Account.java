package stock.mock_stock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long acid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)  // User를 직접 참조하지 않고 uid로 관리
    private User user;

    @Column(columnDefinition = "BIGINT DEFAULT 0")  // 테이블 만들때 DB에서 기본값 0 설정
    @Builder.Default
    private Long balance = 0L;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountTransaction> transactions = new ArrayList<>();

    //TODO : created_at, updated_at 도 추가해서 jpa에서 관리할수있도록 일관성? 유지


}
