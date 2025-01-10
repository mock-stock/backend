package stock.mock_stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import stock.mock_stock.client.KISWebSocketClient;
import stock.mock_stock.common.ApprovalKeyManager;
import stock.mock_stock.dto.StockSubscribeRequestDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebsocketController {

    private final KISWebSocketClient kisWebSocketClient;
    private final ApprovalKeyManager approvalKeyManager;

    @MessageMapping("/stocks") // 클라이언트가 "/app/subscribe"로 메시지를 보낼 때 처리
    @SendTo("/stocks")
    public String handleSubscribeRequest(@Payload StockSubscribeRequestDto request) {

        try{
            System.out.println("Client requested subscription for stock: " + request);
            // 여기서 추가적인 구독 관리 로직 작성 가능
            String action = request.getAction();
            System.out.println("action = " + action);
            List<String> ids = request.getIds();
            System.out.println("ids = " + ids);
            ids.forEach( stockCode ->{
                kisWebSocketClient.subscribeStock(stockCode, approvalKeyManager.getApprovalKey());
            } ); //  TODO : 에러 처리 프론트한테 보여주는 방식 추가 : 별도의 특정 유저에게 전달해야되는데 아무리해도안됨
            return "send completed";
        } catch (Exception e) {
            return "something wrong";
//            throw new RuntimeException(e);
        }

    }

}
