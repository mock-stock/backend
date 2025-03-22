package stock.mock_stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stock.mock_stock.entity.Account;
import stock.mock_stock.entity.User;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserUid(Long uid);

    @Query("SELECT a FROM Account a JOIN FETCH a.user WHERE a.user.uid = :uid")
    Optional<Account> findWithUserByUid(@Param("uid") Long uid); // TODO: 이렇게 JOIN FETCH를 별도로 추가해서 Account 는 LAZY LOADING으로 하면 하고안하고 차이를 알고 성능 최적화를 어떻게했는지 알기위해 테스트 필요,

}
