package stock.mock_stock.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import stock.mock_stock.client.KISWebSocketClient;
import stock.mock_stock.dto.WebApprovalKey;

@Component
@RequiredArgsConstructor
public class ApprovalKeyManager {

    private final KISWebSocketClient kisWebSocketClient;
    private String approvalKey;
    private long expirationTime = 0;

    public synchronized String getApprovalKey(){
        if(approvalKey == null || System.currentTimeMillis()> expirationTime){
            System.out.println("approvalKey = " + approvalKey);
            System.out.println("expirationTime = " + expirationTime);
            System.out.println("(System.currentTimeMillis() > expirationTime) = " + (System.currentTimeMillis() > expirationTime));

            WebApprovalKey fetchedWebApprovalKey = kisWebSocketClient.fetchWebApprovalKey(
                    KISApiConstant.GRANT_TYPE,
                    KISApiConstant.APP_KEY,
                    KISApiConstant.APP_SECRET
            );
            approvalKey = fetchedWebApprovalKey.getApprovalKey();
            expirationTime = System.currentTimeMillis() + 24*60*60*1000; // NOTE: 24시간
        }

        return approvalKey;
    }


}
