package stock.mock_stock.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stock.mock_stock.dto.TestUserInfo;
import stock.mock_stock.dto.TokenInfo;
import stock.mock_stock.entity.User;
import stock.mock_stock.repository.UserRepository;
import stock.mock_stock.security.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/login/kakao")
    public ResponseEntity<Object> kakaoLogin(@RequestHeader("Authorization") String authorizationHeader){
        // TODO: try catch authorization 값에 있고 없고 로직추가 필요
        String accessToken = authorizationHeader.substring(7);
        System.out.println("accessToken = " + accessToken);
        TokenInfo tokenInfo = kakaoAuthService.authenticate(accessToken);

        // HttpOnly 쿠키 설정 (refreshToken)
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenInfo.getRefreshToken())
                .httpOnly(true)  // JavaScript에서 접근 불가
                .secure(true)    // HTTPS에서만 전송 (로컬 테스트 시 false)
                .path("/")       //  쿠키의 유효 경로 설정
                .maxAge(7 * 24 * 60 * 60) // ✅ 쿠키 유효 기간 설정 (7일)
                .sameSite("None") // CSRF 방어 (Strict 또는 Lax)
                .build();
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", tokenInfo.getAccessToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(responseBody);

    }

    @PostMapping("/test")
    public ResponseEntity<Object> testLogin(@RequestBody TestUserInfo testUserInfo){
        try{
            System.out.println("testUserInfo = " + testUserInfo.getEmail());
            System.out.println("testUserInfo = " + testUserInfo.getPassword());
        TokenInfo tokenInfo = localAuthService.testAuthenticate(testUserInfo);

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
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            System.out.println("refreshToken = " + refreshToken);
            return ResponseEntity.status(401).body("Not exist refresh token");
        }
        try{ // TODO: try catch 부분 @ControllerAdvice로 중앙화할것 ExpiredJwtException, JwtException | IllegalArgumentException 등
            Map<String, String> responseBody = new HashMap<>();
        if(jwtTokenProvider.validateToken(refreshToken)){

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            System.out.println("userId = " + userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")); // TODP: 리프레시토큰인데도 못찾았을때 어떻게할지 추가

        // Access Token 재발행
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getUid(),
                user.getEmail(),
                user.getNickname(),
                user.getRole());

        responseBody.put("accessToken", newAccessToken);
        }

        return ResponseEntity.ok().body(responseBody);

        } catch (ExpiredJwtException e) {
            System.out.println("만료된 토큰입니다: " + e.getMessage() + "claims = " + e.getClaims());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Expired Refresh Token");
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("유효하지않은 토큰입니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        }

    }

}
