package stock.mock_stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    @PostMapping("/oauth/kakao")
    public void kakaoLogin(@RequestHeader("Authorization") String authorizationHeader){
        String accessToken = authorizationHeader.replace("Bearer ", "");
        System.out.println("access token = " + accessToken);

    }
}
