package stock.mock_stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stock.mock_stock.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일을 기준으로 사용자 조회 (소셜 로그인 검증 시 활용)
    Optional<User> findByEmail(String email);
}
