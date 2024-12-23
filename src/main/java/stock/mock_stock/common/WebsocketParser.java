package stock.mock_stock.common;

public class WebsocketParser {

    public static void parseMessage(String message) {
        // Step 1: |로 분리
        String[] parts = message.split("\\|");

        // Step 2: 체결 데이터 추출
        String tradeData = parts[3]; // 체결 데이터가 포함된 부분
        String[] fields = tradeData.split("\\^");

        // Step 3: 주요 데이터 출력
        System.out.println("종목 코드: " + fields[0]);   // 005930
        System.out.println("체결 시간: " + fields[1]);   // 111007
        System.out.println("현재 체결가: " + fields[2]); // 53300
        System.out.println("체결 구분: " + fields[3]);   // 5
        System.out.println("전일 대비: " + fields[4]);   // -1600
        System.out.println("등락률: " + fields[5]);      // -2.91

        // Step 3: 주요 데이터 반환
//        return new ParsedMessage(
//                fields[0], // 종목 코드
//                fields[1], // 체결 시간
//                fields[2], // 현재 체결가
//                fields[3], // 체결 구분
//                fields[4], // 전일 대비
//                fields[5]  // 등락률
//        );

    }

}
