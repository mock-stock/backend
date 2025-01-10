package stock.mock_stock.client;

import jakarta.websocket.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import stock.mock_stock.common.WebsocketParser;
import stock.mock_stock.dto.StockInfoDto;
import stock.mock_stock.dto.WebApprovalKey;
import stock.mock_stock.service.KISApiService;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private final SimpMessagingTemplate messagingTemplate;
    private final WebsocketParser websocketParser;
//    private final KISWebSocketClient kisWebSocketClient;
    public void connect(String uri) {
        try {

            // 웹소켓 연결
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxTextMessageBufferSize(512 * 1024); // 512KB로 설정
            container.setDefaultMaxBinaryMessageBufferSize(512 * 1024); // 512KB로 설정
            session = container.connectToServer(this, new URI(uri)); // NOTE: this는 @
            System.out.println("Connected to Korea Investment WebSocket: " + uri);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
            try {
                session.close();
                System.out.println("WebSocket connection closed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void subscribeStock(String stockCode, String approvalKey){
        if(session == null || !session.isOpen()){
            System.err.println("WebSocket session is not connected."); // NOTE: 서버와 KIS연결이 끊겼을때
            LocalTime now = LocalTime.now();
            LocalTime cutoffTime = LocalTime.of(18, 0);
            DayOfWeek today = LocalDate.now().getDayOfWeek();
            if(now.isBefore(cutoffTime) && (today != DayOfWeek.SATURDAY && today != DayOfWeek.SUNDAY)){
                System.out.println("reconnecting");
                String wsUri = "ws://ops.koreainvestment.com:31000/tryitout/H0STCNT0";
                connect(wsUri);
            } else{
                System.out.println("try before 6PM on weekdays");
                throw new RuntimeException("try before 6PM on weekdays");
            }
        }

        try{
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
                            "\"tr_key\":\"%s\"" +
                            "}" +
                            "}" +
                            "}",
                    approvalKey // %s 자리에 approvalKey 값 삽입
                    ,stockCode
            );
            System.out.println("session = " + session);
            session.getAsyncRemote().sendText(subscriptionMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public WebApprovalKey fetchWebApprovalKey(String grantType, String appKey, String appSecret) {
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

    public boolean isSessionOpen() {
        return session != null && session.isOpen();
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message) {
//        System.out.println("Received message: " + message);
        if(isDelimitedMessage(message)){
            processMessage(message);
        }
   if(message.contains("PINGPONG")){
       String pongMessage = "{\"header\":{\"tr_id\":\"PINGPONG\"}}";
       session.getAsyncRemote().sendText(pongMessage);
   }

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
        StockInfoDto parsedStock = websocketParser.parseMessage(message);
//        System.out.println("parsedStock = " + parsedStock);
        messagingTemplate.convertAndSend("/stocks/" + parsedStock.getStckCode(), parsedStock);         // 여기서 받은 메시지를 처리, 브로커로 전달
    }

    // 구분자로 구분된 메시지인지 확인
    public boolean isDelimitedMessage(String message) {
        return message.contains("|") && message.contains("^");
    }

}
