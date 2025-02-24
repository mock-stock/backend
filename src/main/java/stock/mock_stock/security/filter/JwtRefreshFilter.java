package stock.mock_stock.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import stock.mock_stock.security.JwtTokenProvider;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtRefreshFilter extends OncePerRequestFilter {
 // TODO: 리프레시 검증이 제데로 안되는데 일단 config에서 주석처리후 추후 테스트
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1️⃣ RefreshToken은 HttpOnly 쿠키에서 추출
        String refreshToken = getRefreshTokenFromCookie(request);
        String path = request.getRequestURI();
        System.out.println("refreshToken = " + refreshToken);
        System.out.println("path = " + path);
        if (refreshToken != null && path.startsWith("auth/refresh")) {
            System.out.println("path = " + refreshToken);
            try {
                // 2️⃣ JWT 유효성 검증
                if (jwtTokenProvider.validateToken(refreshToken)) {
                    // 3️⃣ 토큰에서 사용자 ID 추출
                    Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
                    System.out.println("userId = " + userId);
                    // 4️⃣ SecurityContext에 사용자 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(userId, null, null)
                    );
                }
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Refresh Token expired\"}");
                return;
            } catch (JwtException | IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Invalid Refresh Token\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // ✅ RefreshToken 쿠키에서 가져오기
    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}