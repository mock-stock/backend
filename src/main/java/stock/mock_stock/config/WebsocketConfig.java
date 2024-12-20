package stock.mock_stock.config;

import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 브로커 설정
        config.enableSimpleBroker("/topic", "/queue"); // 클라이언트가 구독할 수 있는 경로
        config.setApplicationDestinationPrefixes("/app"); // 클라이언트 메시지 발행 경로
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 엔드포인트 등록
        registry.addEndpoint("/websocket") // 클라이언트가 연결할 엔드포인트
                .setAllowedOrigins("*")    // CORS 설정
                .withSockJS();            // SockJS 폴백 지원
    }

}
