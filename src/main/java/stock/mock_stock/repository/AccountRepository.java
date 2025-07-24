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
    Optional<Account> findWithUserByUid(@Param("uid") Long uid); // NOTE: 계좌정보와 회원정보를 동시에 써야할때 Fetch join으로 즉시 호출로 N+1 해결 및 객체지향적 매핑(Account안에 user도 같이)으로 타입 문제 해결

}
