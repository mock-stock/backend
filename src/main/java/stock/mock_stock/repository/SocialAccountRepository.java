package stock.mock_stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stock.mock_stock.entity.SocialAccount;


import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    @Query("SELECT s FROM SocialAccount s JOIN FETCH s.user WHERE s.provider = :provider AND s.providerUserId = :providerUserId")
    Optional<SocialAccount> findByProviderAndProviderUserIdWithUser(@Param("provider") String provider,
                                                                    @Param("providerUserId") Long providerUserId);
}
