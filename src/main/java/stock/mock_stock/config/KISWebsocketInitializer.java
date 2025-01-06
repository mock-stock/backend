package stock.mock_stock.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import stock.mock_stock.service.KISApiService;

@Component
@RequiredArgsConstructor
public class KISWebsocketInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final KISApiService kisApiService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        kisApiService.startKISWebsocket();
    }
}
