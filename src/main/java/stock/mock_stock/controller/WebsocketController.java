package stock.mock_stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import stock.mock_stock.client.KISWebSocketClient;
import stock.mock_stock.common.ApprovalKeyManager;
import stock.mock_stock.dto.StockSubscribeRequestDto;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebsocketController {

    private final KISWebSocketClient kisWebSocketClient;
    private final ApprovalKeyManager approvalKeyManager;

    @MessageMapping("/stocks") // 클라이언트가 "/app/subscribe"로 메시지를 보낼 때 처리
    @SendTo("/stocks")
    public String handleSubscribeRequest(@Payload StockSubscribeRequestDto request) {
        System.out.println("Client requested subscription for stock: " + request);
        // 여기서 추가적인 구독 관리 로직 작성 가능
        String action = request.getAction();
        System.out.println("action = " + action);
        List<String> ids = request.getIds();
        System.out.println("ids = " + ids);
        ids.forEach( stockCode ->{
            kisWebSocketClient.subscribeStock(stockCode, approvalKeyManager.getApprovalKey());
        } );

        return "send completed";
    }
}
