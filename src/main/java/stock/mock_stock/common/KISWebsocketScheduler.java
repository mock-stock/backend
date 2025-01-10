package stock.mock_stock.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import stock.mock_stock.client.KISWebSocketClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class KISWebsocketScheduler {

    private final KISWebSocketClient kisWebSocketClient;

    @Scheduled(cron = "0 55 8 * * ?")
    public void connectKISWebsocket(){
        if(!kisWebSocketClient.isSessionOpen()){
            try{
                System.out.println("Scheduled task: connecting Websocket at 8:55 AM");
                String wsUri = "ws://ops.koreainvestment.com:31000/tryitout/H0STCNT0";
                kisWebSocketClient.connect(wsUri);
            }   catch (Exception e) {
                log.error("에러발생", e);
            }
        }
    }

    @Scheduled(cron = "0 0 18 * * ?")
    public void disconnectKISWebsocket(){
        System.out.println("Scheduled task: Disconnecting WebSocket at 6 PM");
        if (kisWebSocketClient.isSessionOpen()) {
         try{
            kisWebSocketClient.closeConnection();
        } catch (Exception e) {
           log.error("예외발생:", e);
        }
        } else {
            System.out.println("WebSocket is already closed or not initialized.");
        }
    }

    @Scheduled(cron ="0 7 12 * * ?")
    public void testScheduled(){
        System.out.println("test!!!!");
    }

}
