package stock.mock_stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KISApiServiceImpl implements KISApiService{

    @Value("${api.korea-investment.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;


    @Override
    public String getDomesticStockInfo(String fidCondMrktDivCode, String fidInputIscd) {
        // URL 구성
        String url = baseUrl + "/uapi/domestic-stock/v1/quotations/inquire-price"
                + "?FID_COND_MRKT_DIV_CODE=" + fidCondMrktDivCode
                + "&FID_INPUT_ISCD=" + fidInputIscd;

        // Header 구성
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0b2tlbiIsImF1ZCI6IjQzMTQ1NjJjLTVmOWUtNDg5OC04MGVmLWEyZTEwZjEwNTVmOCIsInByZHRfY2QiOiIiLCJpc3MiOiJ1bm9ndyIsImV4cCI6MTczNDQxMTA5NywiaWF0IjoxNzM0MzI0Njk3LCJqdGkiOiJQU1lmTTBtQXVFTXRhaHdhZmJNbUV5MzFRc3VyOGFkVGNvVDQifQ.yT-RBH4LQM1mY-RsOZCxEbQYRkFaxt1BD4AaHxG4irjjrT-Edn8bcNcPHJi72m0Pq9QJZpnJtxPsez6wcA6ZXQ");
        headers.set("appkey", "PSYfM0mAuEMtahwafbMmEy31Qsur8adTcoT4");
        headers.set("appsecret", "S0K6261WIaovRCI3ZJ/I+akFDvuEU8KXeVKUShngGid8pc1RHln05pA754Jct3UMGRwbRAp5BeovIJxDhVJYI/FHzDe33GrUFVajfF9nwMR0mowkU+dDf4PSYQiqkaaQbhSE+dLrPwYEFkPVrHhgDxniOCV3Ry8x17ZncKK2jlysD9cVmYY=");
        headers.set("tr_id", "FHKST01010100");

        // HttpEntity 생성 (Header 포함)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // API 호출
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
}
