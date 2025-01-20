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
import stock.mock_stock.common.StockValidator;
import stock.mock_stock.common.TokenStorage;
import stock.mock_stock.dto.OAuthToken;
import stock.mock_stock.dto.StockInfoOutput;
import stock.mock_stock.dto.StockKisDto;
import stock.mock_stock.dto.StockKisHistoryDto;
import stock.mock_stock.entity.Stock;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KISApiServiceImpl implements KISApiService{

    @Value("${api.korea-investment.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final TokenStorage tokenStorage;
    private final KISWebSocketClient kisWebSocketClient;
    private final StockValidator stockValidator;

    @Override
    public StockInfoOutput getDomesticStockInfo(String fidCondMrktDivCode, String fidInputIscd) {
        // URL 구성
        String url = baseUrl + "/uapi/domestic-stock/v1/quotations/inquire-price"
                + "?FID_COND_MRKT_DIV_CODE=" + fidCondMrktDivCode
                + "&FID_INPUT_ISCD=" + fidInputIscd;

        // NOTE: 유효한 토큰있을시 스토리지에서 가져옴
        OAuthToken token = getoAuthToken();;

        // Header 구성
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ token.getAccessToken());
        headers.set("appkey", KISApiConstant.APP_KEY);
        headers.set("appsecret", KISApiConstant.APP_SECRET);
        headers.set("tr_id", KISApiConstant.TR_ID_INQUIRE_PRICE);

        // HttpEntity 생성 (Header 포함)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<StockInfoOutput> response;
        try{
        // API 호출
        response = restTemplate.exchange(url, HttpMethod.GET, entity, StockInfoOutput.class); // 한투에서 output상위 속성이있어 상위클래스 StockInfoOuput으로 매핑되도록 설정
//        System.out.println("response = " + response.getBody());

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

    private OAuthToken getoAuthToken() {
        OAuthToken token;
        if(checkTokenAvailable("kis_token")){
            token = getTokenInfo("kis_token");
        } else{ // NOTE: 유효한 토큰이없을시 Fetch
          token = fetchOauthToken(KISApiConstant.GRANT_TYPE, KISApiConstant.APP_KEY, KISApiConstant.APP_SECRET );
        }
        return token;
    }

    @Override
    public List<StockKisHistoryDto> getStockHistoryInfo(String fidCondMrktDivCode, String fidInputIscd, LocalDate fidInputDate1, LocalDate fidInputDate2, String interval) {
        Stock stock = stockValidator.getStock(fidInputIscd);
        String url;
        // NOTE: 유효한 토큰있을시 스토리지에서 가져옴
        OAuthToken token = getoAuthToken();;

        // Header 구성
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity;
        List<StockKisHistoryDto> allData = new ArrayList<>();
        ResponseEntity<StockInfoOutput> response;

        // LocalDate를 yyyyMMdd 형식으로 변환
        String formattedFromDate = fidInputDate1.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String formattedToDate = fidInputDate2.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String toDateTime = "153000"; // NOTE: 국내주식 15:30:00 장마감 시간으로 설정

        headers.set("Authorization", "Bearer "+ token.getAccessToken());
        headers.set("appkey", KISApiConstant.APP_KEY);
        headers.set("appsecret", KISApiConstant.APP_SECRET);


        // 1일별 분봉 조회 로직
        if("MINUTE".equals(interval)){
         while (true){
             url= baseUrl + "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice"
                     + "?FID_COND_MRKT_DIV_CODE=" + fidCondMrktDivCode
                     + "&FID_INPUT_ISCD=" + fidInputIscd
                     + "&FID_INPUT_DATE_1=" + formattedFromDate
                     + "&FID_INPUT_HOUR_1=" + toDateTime
                     + "&FID_PERIOD_DIV_CODE=" + interval
                     + "&FID_PW_DATA_INCU_YN=N"
                     + "&FID_FAKE_TICK_INCU_YN=N";

             headers.set("tr_id", KISApiConstant.TR_ID_INQUIRE_DAILY_ITEMCHART_PRICE_MINUTE);
             headers.set("custtype", "P");

             // HttpEntity 생성 (Header 포함)
             entity = new HttpEntity<>(headers);

             try{
                 // API 호출
                 response = restTemplate.exchange(url, HttpMethod.GET, entity, StockInfoOutput.class); // 한투에서 output상위 속성이있어 상위클래스 StockInfoOuput으로 매핑되도록 설정
                 allData.addAll(response.getBody().getStockKisHistoryDto());

                 String lastHour = allData.get(allData.size()-1).getStckCntgHour();

                 if(lastHour.compareTo("090000") <= 0){
                     break;
                 } else{
                     toDateTime = lastHour;

                     Thread.sleep(500); // NOTE: 0.5초 대기, 이유: 한국투자증권 초당 요청건수 한계가있어서 어느정도 텀을 주기위해, 0.4초로하면 될때도있고 통과못할때도있기때문에 0.5로
                     // TODO: 한국투자에 공통키로 서버쪽에서 유저가 요청하는만큼 요청해도 초당 요청한계가있어서 에러가 발생 만약 10명만 요청을 한번에 보내도 해당 리밋초과이기 떄문 이에 대한 해결책 강구필요
                 }
                 System.out.println("here");
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
             } catch (InterruptedException e) {
                 throw new RuntimeException(e);
             }
         }
        } else {

        // URL 구성
         url = baseUrl + "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice"
                + "?FID_COND_MRKT_DIV_CODE=" + fidCondMrktDivCode
                + "&FID_INPUT_ISCD=" + fidInputIscd
                + "&FID_INPUT_DATE_1=" + formattedFromDate
                + "&FID_INPUT_DATE_2=" + formattedToDate
                + "&FID_PERIOD_DIV_CODE=" + interval
                + "&FID_ORG_ADJ_PRC=0";


        headers.set("tr_id", KISApiConstant.TR_ID_INQUIRE_DAILY_ITEMCHART_PRICE);

            // HttpEntity 생성 (Header 포함)
            entity = new HttpEntity<>(headers);

            try{
                // API 호출
                response = restTemplate.exchange(url, HttpMethod.GET, entity, StockInfoOutput.class); // 한투에서 output상위 속성이있어 상위클래스 StockInfoOuput으로 매핑되도록 설정

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
        }



        if("MINUTE".equals(interval)){ // NOTE : 일별본붕으로 요청한 경우
            return allData.stream().peek(dto -> {
                dto.setStckName(stock.getStckName());
                dto.setStckClpr(dto.getStckPrpr()); // 현재가가 종가 마지막가이기떄문에
                // NOTE: 공식 : 일분봉의 경우 (마지막종가(현재가) - 시작가) / 시작가 * 100 = stck_prpr - stck_oprc/stck_oprc(시작가)
                Double changedRate = ((double)(dto.getStckClpr() - dto.getStckOprc())/(double) dto.getStckOprc()) * 100;
                dto.setStckChangeRate(Math.round(changedRate*100)/100.0); // 두자리수만 표시하기위해
                dto.setAcmlVol(dto.getCntgVol()); // KIS에서 주는 변수명이 다르지만 결국 누적량은 같기에 acmlVol로 Set해줌
                dto.setPrdyVrss(dto.getStckClpr() - dto.getStckOprc());
            }).toList();
        }
        else{
            return response.getBody().getStockKisHistoryDto().stream().peek(dto -> {
                dto.setStckName(stock.getStckName());
                // NOTE: 공식 : 전일대비액(오늘종가 - 어제종가) / 어제종가 * 100 = prdy_vrss(종가차액)/stck_clpr - prdy_vrss(현재종가 - 종가대비 = 어제종가), 어제종가액을 주지않기에 이런식으로 공식만들어 사용
                Double changedRate = ((double)dto.getPrdyVrss()/(double)(dto.getStckClpr() - dto.getPrdyVrss())) * 100;
                dto.setStckChangeRate(Math.round(changedRate*100)/100.0); // 두자리수만 표시하기위해
            }).toList();
        }
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

    @Override
    public String fetchWebApprovalKey(String grantType, String appKey, String appSecret) {
        String url = baseUrl + "/oauth2/Approval";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", grantType);
        requestBody.put("appkey", appKey);
        requestBody.put("appsecret", appSecret);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // JSON 형식의 데이터 전송

        // 요청 엔티티 생성
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return response.getBody();
    }


}
