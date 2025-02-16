package stock.mock_stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import stock.mock_stock.dto.KakaoUserInfo;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthServiceImpl implements KakaoAuthService{

    private final RestTemplate restTemplate;

    @Override
    public KakaoUserInfo authenticate(String accessToken) {
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(accessToken);
        System.out.println("kakaoUserInfo = " + kakaoUserInfo);
        return kakaoUserInfo;
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
