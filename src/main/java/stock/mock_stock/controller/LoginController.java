package stock.mock_stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stock.mock_stock.dto.TestUserInfo;
import stock.mock_stock.dto.TokenInfo;
import stock.mock_stock.service.KakaoAuthService;
import stock.mock_stock.service.LocalAuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {
    private final KakaoAuthService kakaoAuthService;
    private final LocalAuthService localAuthService;

    @PostMapping("/login/kakao")
    public ResponseEntity<Object> kakaoLogin(@RequestHeader("Authorization") String authorizationHeader){
        String accessToken = authorizationHeader.replace("Bearer ", "");
        System.out.println("access token = " + accessToken);
        TokenInfo tokenInfo = kakaoAuthService.authenticate(accessToken);

        // HttpOnly 쿠키 설정 (refreshToken)
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenInfo.getRefreshToken())
                .httpOnly(true)  // JavaScript에서 접근 불가
                .secure(true)    // HTTPS에서만 전송 (로컬 테스트 시 false)
                .path("/")       //  쿠키의 유효 경로 설정
                .maxAge(7 * 24 * 60 * 60) // ✅ 쿠키 유효 기간 설정 (7일)
                .sameSite("Strict") // ✅ CSRF 방어 (Strict 또는 Lax)
                .build();
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", tokenInfo.getAccessToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(responseBody);

    }

    @PostMapping("/test")
    public ResponseEntity<Object> testLogin(@RequestBody TestUserInfo testUserInfo){
        System.out.println("getEmail = " + testUserInfo.getEmail());
        System.out.println("getPassword = " + testUserInfo.getPassword());
        TokenInfo tokenInfo = localAuthService.testAuthenticate(testUserInfo);
        System.out.println("getAccessToken = " + tokenInfo.getAccessToken());
        System.out.println("getRefreshToken = " + tokenInfo.getRefreshToken());
        // HttpOnly 쿠키 설정 (refreshToken)
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenInfo.getRefreshToken())
                .httpOnly(true)  //  JavaScript에서 접근 불가
                .secure(true)    // HTTPS에서만 전송 (로컬 테스트 시 false)
                .path("/")       //  쿠키의 유효 경로 설정
                .maxAge(7 * 24 * 60 * 60) // 쿠키 유효 기간 설정 (7일)
                .sameSite("None") // CSRF 방어 (Strict 또는 Lax)
                .build();

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", tokenInfo.getAccessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(responseBody);

    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            System.out.println("refreshToken = " + refreshToken);
            return ResponseEntity.status(401).body("Refresh Token이 없습니다.");
        }
        System.out.println("refreshToken = " + refreshToken);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("refreshToken", refreshToken);
        return ResponseEntity.ok().body(responseBody);
    }

}
