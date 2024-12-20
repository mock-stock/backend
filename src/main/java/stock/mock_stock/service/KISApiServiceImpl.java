package stock.mock_stock.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import stock.mock_stock.client.KISWebSocketClient;
import stock.mock_stock.common.KISApiConstant;
import stock.mock_stock.common.TokenStorage;
import stock.mock_stock.dto.OAuthToken;
import stock.mock_stock.dto.StockInfoOutput;
import stock.mock_stock.dto.StockKisDto;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KISApiServiceImpl implements KISApiService{

    @Value("${api.korea-investment.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final TokenStorage tokenStorage;
    private final KISWebSocketClient kisWebSocketClient;


    @Override
    public StockInfoOutput getDomesticStockInfo(String fidCondMrktDivCode, String fidInputIscd) {
        // URL 구성
        String url = baseUrl + "/uapi/domestic-stock/v1/quotations/inquire-price"
                + "?FID_COND_MRKT_DIV_CODE=" + fidCondMrktDivCode
                + "&FID_INPUT_ISCD=" + fidInputIscd;

        OAuthToken token;
        // NOTE: 유효한 토큰있을시 스토리지에서 가져옴
        if(checkTokenAvailable("kis_token")){
            token = getTokenInfo("kis_token");
        } else{ // NOTE: 유효한 토큰이없을시 Fetch
          token = fetchOauthToken(KISApiConstant.GRANT_TYPE, KISApiConstant.APP_KEY, KISApiConstant.APP_SECRET );
        }

        // Header 구성
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ token.getAccessToken());
        headers.set("appkey", KISApiConstant.APP_KEY);
        headers.set("appsecret", KISApiConstant.APP_SECRET);
        headers.set("tr_id", KISApiConstant.TR_ID);

        // HttpEntity 생성 (Header 포함)
        HttpEntity<String> entity = new HttpEntity<>(headers);
        System.out.println("entity = " + entity);
        ResponseEntity<StockInfoOutput> response;
        try{
        // API 호출
        response = restTemplate.exchange(url, HttpMethod.GET, entity, StockInfoOutput.class); // 한투에서 output상위 속성이있어 상위클래스 StockInfoOuput으로 매핑되도록 설정
        System.out.println("response = " + response.getBody());

        } catch (HttpServerErrorException e) {
            // 예외로부터 응답 본문(JSON)을 추출
            String responseBody = e.getResponseBodyAsString();
            System.out.println("Error Response: " + responseBody);

            // JSON 파싱
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                // 특정 값 추출 (예: "msg_cd")
                String msgCode = jsonNode.get("msg_cd").asText();

                // msg_cd 값에 따른 로직 처리
                if ("EGW00123".equals(msgCode)) {
                    System.out.println("토큰 만료: 토큰 재발급 로직 실행");
                    // 재발급 로직 구현
//                    getDomesticStockInfo();
                } else {
                    System.out.println("기타 오류 처리: " + msgCode);
                }
            } catch (Exception ex) {
                System.err.println("JSON 파싱 오류: " + ex.getMessage());
            }
            return null;
        }
        return response.getBody();
    }

    @Override
    public OAuthToken fetchOauthToken(String grantType, String appKey, String appSecret) {
        OAuthToken token;
        String url = baseUrl + "/oauth2/tokenP"; // base_url + endpoint
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", grantType);
        requestBody.put("appkey", appKey);
        requestBody.put("appsecret", appSecret);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // JSON 형식의 데이터 전송

        // 요청 엔티티 생성
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<OAuthToken> response;
        try{
            response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    OAuthToken.class
            );

            // 결과 확인
             token = response.getBody();
            if (token != null) {
                System.out.println("Access Token: " + token.getAccessToken());
                System.out.println("Access Token Expired: " + token.getAccessTokenExpired());
                System.out.println("Token Type: " + token.getTokenType());
                System.out.println("Expires In: " + token.getExpireIn());
            }
        saveTokenInfo("kis_token", token);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return token;
    }

    @Override
    public OAuthToken getTokenInfo(String key) {
        return tokenStorage.getToken(key);
    }

    @Override
    public void saveTokenInfo(String key, OAuthToken oAuthToken) {
        tokenStorage.saveToken(key ,oAuthToken);
    }

    @Override
    public boolean checkTokenAvailable(String key) {
        return tokenStorage.isTokenAvailable(key);
    }

    @Override
    public void startKISWebsocket() {
        String wsUri = "ws://ops.koreainvestment.com:31000/tryitout/H0STCNT0";
        kisWebSocketClient.connect(wsUri);
    }


}
