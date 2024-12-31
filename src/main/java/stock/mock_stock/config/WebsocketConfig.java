package stock.mock_stock.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 브로커 설정
        config.enableSimpleBroker("/stocks"); // 클라이언트가 구독할 수 있는 경로
        config.setApplicationDestinationPrefixes("/api"); // 클라이언트 메시지 발행 경로
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 엔드포인트 등록
        System.out.println("registry = " + registry);
        registry.addEndpoint("/stocks") // 클라이언트가 연결할 엔드포인트
                .setAllowedOriginPatterns("*") // 모든 출처 허용
                .withSockJS();            // SockJS 폴백 지원, 이걸 빼야 postman에서 되고, 넣어야 브라우저 및 클라이언트코드에서 가능 없을시 에러
//        ;
    }

}
