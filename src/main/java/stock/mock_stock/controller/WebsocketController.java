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
//    @SendTo("/stocks") // NOTE: 어차피 공통으로 보내줄 채널이 아니므로 주석처리
    public void handleSubscribeRequest(@Payload StockSubscribeRequestDto request) {
            String action = request.getAction();
            List<String> ids = request.getIds();
            System.out.println("ids = " + ids);
    if(action !=null && ids != null){
        try{
            System.out.println("Client requested subscription for stock: " + request);
            // 여기서 추가적인 구독 관리 로직 작성 가능
            System.out.println("action = " + action);
            ids.forEach( stockCode ->{
                kisWebSocketClient.subscribeStock(stockCode, approvalKeyManager.getApprovalKey());
            } );
//            return "ok";
        } catch (Exception e) {
            System.out.println("e = " + e);
//            return "error:" + e; //  TODO : 에러 처리 프론트한테 보여주는 방식 추가 : 별도의 특정 유저에게 전달해야되는데 아무리해도안됨, text로 error포함되는지 체크, 근데 특정 게스트한테 보내야하므로 그부분 넣도록 수정필요
//            throw new RuntimeException(e);
        }
    }
    else{
        System.out.println("request payloads are wrong" );
        System.out.println("ids = " + ids);
        System.out.println("action = " + action);
//        return "error: \n" + "action = " + action +"\n ids="+ ids ;
    }
    }

}
