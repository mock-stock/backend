package stock.mock_stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stock.mock_stock.dto.TestUserInfo;
import stock.mock_stock.dto.TokenInfo;
import stock.mock_stock.entity.User;
import stock.mock_stock.repository.UserRepository;
import stock.mock_stock.security.JwtTokenProvider;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocalAuthServiceImpl implements LocalAuthService{

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public TokenInfo testAuthenticate(TestUserInfo testUserInfo) {

        Optional<User> existingTestUser = userRepository.findByEmailAndPassword(testUserInfo.getEmail(), testUserInfo.getPassword());
        User user;
        if (existingTestUser.isPresent()) {
            // 기존 User와 새로운 소셜 계정 연결
            user = existingTestUser.get();
            System.out.println("테스트용 유저: " + user);
        } else {
            System.out.println("틀린 테스트 정보입니다.");
            throw new RuntimeException();
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getUid(), user.getEmail(), user.getNickname(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUid());

        return new TokenInfo(accessToken, refreshToken);
    }
}
