package stock.mock_stock.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import stock.mock_stock.service.KISApiService;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class KISWebsocketInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final KISApiService kisApiService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LocalTime now = LocalTime.now();
        LocalTime cutoffTime = LocalTime.of(18, 0);
        if(now.isBefore(cutoffTime)){
            System.out.println("Initializing WebSocket connection as the time is before 6 PM.");
            kisApiService.startKISWebsocket();
        } else{
            System.out.println("Skipping WebSocket initialization as the time is after 6 PM.");
        }
    }
}
