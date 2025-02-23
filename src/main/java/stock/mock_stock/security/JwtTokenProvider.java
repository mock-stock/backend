package stock.mock_stock.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import stock.mock_stock.entity.Role;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60; // 1시간
    private final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7; // 7일

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET 값이 설정되지 않았습니다.");
        }
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(decodedKey);
//        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, String email, String nickname, Role role) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(String.valueOf(userId))
//                .claim("provider", provider) // 불필요정보
                .claim("email", email)
                .claim("nickname", nickname) // 추후 권한도 필요다하면 roles 같은거 추가
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId) { // refresh token의 경우는 userId를 제외 정보 최소화
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e){
            System.out.println("만료된 토큰입니다: " + e.getMessage());
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("유효하지 않은 토큰입니다: " + e.getMessage());
            throw e;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // JWT 토큰에서 사용자 이름 추출
    public String getNickname(String token) {
        return getClaims(token).getSubject(); // subject에 사용자의 이름(또는 ID)을 저장
    }

}