package stock.mock_stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stock.mock_stock.dto.KakaoUserInfo;
import stock.mock_stock.dto.TokenInfoDto;
import stock.mock_stock.service.KakaoAuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/oauth/kakao")
    public ResponseEntity<TokenInfoDto> kakaoLogin(@RequestHeader("Authorization") String authorizationHeader){
        String accessToken = authorizationHeader.replace("Bearer ", "");
        System.out.println("access token = " + accessToken);
        TokenInfoDto tokenInfoDto = kakaoAuthService.authenticate(accessToken);
        return ResponseEntity.ok(tokenInfoDto);

    }
}
