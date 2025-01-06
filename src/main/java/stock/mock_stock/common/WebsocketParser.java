package stock.mock_stock.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import stock.mock_stock.dto.StockInfoDto;

@Component
@RequiredArgsConstructor
public class WebsocketParser {

    private final StockNameCache stockNameCache;

    public StockInfoDto parseMessage(String message) {
        // Step 1: |로 분리
        String[] parts = message.split("\\|");

        // Step 2: 체결 데이터 추출
        String tradeData = parts[3]; // 체결 데이터가 포함된 부분
        String[] fields = tradeData.split("\\^");

        // Step 3: 주요 데이터 출력
//        System.out.println("종목 코드: " + fields[0]);   // 005930
//        System.out.println("체결 시간: " + fields[1]);   // 111007
//        System.out.println("현재 체결가: " + fields[2]); // 53300
//        System.out.println("체결 구분: " + fields[3]);   // 5
//        System.out.println("전일 대비: " + fields[4]);   // -1600
//        System.out.println("등락률: " + fields[5]);      // -2.91
        String stckCode = fields[0];
        String stockName = stockNameCache.getStockNameAndSid(stckCode).getStckName();
        Long stockId = stockNameCache.getStockNameAndSid(stckCode).getSid();
        Long stckCurPrice = Long.parseLong(fields[2]);
        Long stckPrevClsDiffPrice = Long.parseLong(fields[4]);
        Double stckPrevClsDiffPercent = Double.parseDouble(fields[5]);
        // Step 3: StockInfoDto 생성 및 반환
        return new StockInfoDto(
                stockId,
                stockName != null ? stockName : "Unknown",
                stckCode,                 // stckCode: 종목 코드
                stckCurPrice, // stckCurPrice: 현재 체결가
                stckPrevClsDiffPrice, // stckPrevClsDiffPrice: 전일 대비
                stckPrevClsDiffPercent); // stckPrevClsDiffPercent: 등락률

    }

}
