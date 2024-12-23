package stock.mock_stock.client;

import jakarta.websocket.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import stock.mock_stock.common.KISApiConstant;
import stock.mock_stock.dto.WebApprovalKey;
import stock.mock_stock.service.KISApiService;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@ClientEndpoint
@Component
@RequiredArgsConstructor
public class KISWebSocketClient {

    private Session session;
    @Value("${api.korea-investment.base-url}")
    private String baseUrl;
    private final RestTemplate restTemplate;
    public void connect(String uri) {
        try {

            // 웹소켓 연결
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, new URI(uri)); // NOTE: this는 @
            System.out.println("Connected to Korea Investment WebSocket: " + uri);

            WebApprovalKey approvalKey = fetchWebApprovalKey(KISApiConstant.GRANT_TYPE, KISApiConstant.APP_KEY, KISApiConstant.APP_SECRET);

            // 웹소켓 연결후 KIS서버로 구독 요청 메시지 전송
            String subscriptionMessage = String.format(
                    "{" +
                            "\"header\":{" +
                            "\"approval_key\":\"%s\"," + // approvalKey 변수 삽입
                            "\"custtype\":\"P\"," +
                            "\"tr_type\":\"1\"," +
                            "\"content-type\":\"utf-8\"" +
                            "}," +
                            "\"body\":{" +
                            "\"input\":{" +
                            "\"tr_id\":\"H0STCNT0\"," +
                            "\"tr_key\":\"005935\"" +
                            "}" +
                            "}" +
                            "}",
                    approvalKey.getApprovalKey() // %s 자리에 approvalKey 값 삽입
            );
            session.getAsyncRemote().sendText(subscriptionMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WebApprovalKey fetchWebApprovalKey(String grantType, String appKey, String appSecret) {
        String url = baseUrl + "/oauth2/Approval";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", grantType);
        requestBody.put("appkey", appKey);
        requestBody.put("secretkey", appSecret);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // JSON 형식의 데이터 전송

        // 요청 엔티티 생성
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<WebApprovalKey> response;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    WebApprovalKey.class
            );
            System.out.println("response = " + response.getBody());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return response.getBody();
    }


    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);

        // 여기서 받은 메시지를 처리하거나 브로커로 전달
        processMessage(message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket connection closed: " + closeReason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    private void processMessage(String message) {
        // 받은 메시지를 처리하거나 Spring 메시지 브로커로 전달
        System.out.println("Processing message: " + message);
    }

}
