package stock.mock_stock.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ms_user")  // 실제 테이블 이름과 매핑
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;  // PRIMARY KEY

    @Column(nullable = false, unique = true)
    private String email;  // 이메일 (회원 고유 식별값)

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = true)  // NULL 허용 (비밀번호는 소셜로그인 사용 시 없을 수도 있음)
    private String password;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  //

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();  // 엔티티가 수정될 때 변경 필요

    // Role 추가 (EnumType.STRING으로 저장)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER; // 기본값은 일반 사용자(USER)

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Watchlist> watchlist = new ArrayList<>();

    // User와 SocialAccount 간의 관계 (1:N)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "acid", nullable = false)
    private Account account;  //

    // JPA가 INSERT할 때 자동으로 `createdAt`을 현재 시간으로 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // JPA가 UPDATE할 때 `updatedAt`을 자동으로 갱신
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}