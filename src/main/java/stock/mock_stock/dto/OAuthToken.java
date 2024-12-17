package stock.mock_stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class OAuthToken {

    @JsonProperty("access_token") // JSON 키와 필드 매핑
    private String accessToken;
    @JsonProperty("access_token_token_expired")
    private String accessTokenExpired;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private Long expireIn;

    OAuthToken(){}

    public OAuthToken(String accessToken, String accessTokenExpired, String tokenType, Long expireIn){
        this.accessToken = accessToken;
        this.accessTokenExpired = accessTokenExpired;
        this.tokenType = tokenType;
        this.expireIn = expireIn;
    }

    public boolean isExpired(){

        // "2024-12-17 13:51:37" 형식의 문자열을 파싱
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expirationTime = LocalDateTime.parse(accessTokenExpired, formatter);

        // LocalDateTime을 Epoch Milliseconds로 변환
        long expiresAt = expirationTime
                .atZone(ZoneId.systemDefault()) // 시스템 기본 타임존 적용
                .toInstant()
                .toEpochMilli();

        // 현재 시간과 비교하여 만료 여부 반환
        return System.currentTimeMillis() > expiresAt;
    }


}
