package stock.mock_stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import stock.mock_stock.dto.KakaoUserInfo;
import stock.mock_stock.service.KakaoAuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/oauth/kakao")
    public void kakaoLogin(@RequestHeader("Authorization") String authorizationHeader){
        String accessToken = authorizationHeader.replace("Bearer ", "");
        System.out.println("access token = " + accessToken);
        KakaoUserInfo authResponse = kakaoAuthService.authenticate(accessToken);
        System.out.println("getEmail = " + authResponse.getEmail());
        System.out.println("getNickname = " + authResponse.getNickname());
        System.out.println("getId = " + authResponse.getId());

    }
}
