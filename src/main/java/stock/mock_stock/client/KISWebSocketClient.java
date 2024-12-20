package stock.mock_stock.client;

import jakarta.websocket.*;
import org.springframework.stereotype.Component;

import java.net.URI;

@ClientEndpoint
@Component
public class KISWebSocketClient {

    private Session session;

    public void connect(String uri) {
        try {

            // 웹소켓 연결
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, new URI(uri)); // NOTE: this는 @
            System.out.println("Connected to Korea Investment WebSocket: " + uri);

            // 웹소켓 연결후 KIS서버로 구독 요청 메시지 전송
            String subscriptionMessage = "{" +
                    "\"header\":{" +
                    "\"approval_key\":\"79974d9d-fac1-4928-8bd6-083fa458aca6\"," +
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
                    "}";
            session.getAsyncRemote().sendText(subscriptionMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
