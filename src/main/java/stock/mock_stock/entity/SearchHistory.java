package stock.mock_stock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "search_history")
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fid;

    @ManyToOne(fetch = FetchType.LAZY) // NOTE: uid를 제외하곤 user정보가 딱히 필요없기떄문에
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 255)
    private String searchQuery;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public SearchHistory(User user, String searchQuery, LocalDateTime createdAt){
        this.user = user;
        this.searchQuery = searchQuery;
        this.createdAt = createdAt;
    }
}
