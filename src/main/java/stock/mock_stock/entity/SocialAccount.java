package stock.mock_stock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "social_account")
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long socialId; // 소셜 계정의 PK (자동 증가)

    @ManyToOne  //  SocialAccount(다) : User(1) 관계
    @JoinColumn(name = "uid", nullable = false)  // 실제 테이블의 uid 컬럼과 매핑
    private User user;

    @Column(nullable = false)
    private String provider; // kakao, naver, google, apple

    @Column(nullable = false, unique = true)
    private Long providerUserId; // 소셜 로그인에서 제공하는 사용자 고유 ID

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    @Builder
    public SocialAccount(User user, String provider, Long providerUserId) {
        this.user = user;
        this.provider = provider;
        this.providerUserId = providerUserId;
    }

}