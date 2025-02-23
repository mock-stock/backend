package stock.mock_stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import stock.mock_stock.dto.KakaoUserInfo;
import stock.mock_stock.dto.TokenInfo;
import stock.mock_stock.entity.SocialAccount;
import stock.mock_stock.entity.User;
import stock.mock_stock.repository.SocialAccountRepository;
import stock.mock_stock.repository.UserRepository;
import stock.mock_stock.security.JwtTokenProvider;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoAuthServiceImpl implements KakaoAuthService{

    private final RestTemplate restTemplate;
    private final SocialAccountRepository socialAccountRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    @Transactional // 유저정보와 소셜계정정보 저장이 각각 있는데 하나라도 실패하면 rollback등 유지하기위해
    public TokenInfo authenticate(String KakaoaccessToken) {
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(KakaoaccessToken);
        System.out.println("kakaoUserInfo = " + kakaoUserInfo);
        Optional<SocialAccount> socialAccount = socialAccountRepository.findByProviderAndProviderUserIdWithUser("kakao", kakaoUserInfo.getId());

        User user;
        if (socialAccount.isPresent()) {
            // 기존 회원일 경우
            user = socialAccount.get().getUser();
            System.out.println("기존 회원 정보 = " + user);
        } else {
            // 기존 회원이 없으면 email 기반으로 회원 검색
            Optional<User> existingUser = userRepository.findByEmail(kakaoUserInfo.getEmail());

            if (existingUser.isPresent()) {
                // 기존 User와 새로운 소셜 계정 연결
                user = existingUser.get();
                System.out.println("기존 회원이 존재하여 소셜 계정 추가: " + user);
            } else {
                // 새로운 회원 생성
                user = User.builder()
                        .email(kakaoUserInfo.getEmail())
                        .nickname(kakaoUserInfo.getNickname())
                        .build();
                userRepository.save(user);
                System.out.println("신규 회원 등록 완료: " + user);
            }

            // 새로운 SocialAccount 저장
            SocialAccount newSocialAccount = SocialAccount.builder()
                    .user(user)  // 기존 or 신규 User와 연결
                    .provider("kakao")
                    .providerUserId(kakaoUserInfo.getId())
                    .build();
            socialAccountRepository.save(newSocialAccount);
            System.out.println("소셜 계정 등록 완료: " + newSocialAccount);
        }

        // 6️⃣ JWT 발급 후 AuthResponse 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getUid(), user.getEmail(), user.getNickname(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUid());

        return new TokenInfo(accessToken, refreshToken);
    }

    @Override
    public KakaoUserInfo getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 카카오 API 호출
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
        );

        // 응답 파싱
        Map<String, Object> body = response.getBody();
        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return new KakaoUserInfo(
                Long.parseLong(body.get("id").toString()),
                (String) profile.get("nickname"),
                (String) kakaoAccount.get("email")
        );
    }

}
