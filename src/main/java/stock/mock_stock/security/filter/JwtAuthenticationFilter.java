package stock.mock_stock.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        // 1. 헤더에서 JWT 추출 (Bearer Token 방식)
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            //  2. JWT 유효성 검증
            try{
                if (jwtTokenProvider.validateToken(token)) {
                    //  3. 토큰에서 사용자 ID 추출
                    Long userId = jwtTokenProvider.getUserIdFromToken(token);

                    //  4. SecurityContext에 사용자 인증 정보 저장 (권한이 없을 시 null로 처리 가능)
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, null);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                // 401 Unauthorized: 토큰이 만료됨
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Unauthorized: Token has expired");
                return;
            } catch (JwtException | IllegalArgumentException e) {
                // 401 Unauthorized: 기타 JWT 예외 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Unauthorized: JWT error");
                return;
            }

    }
        //  5. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
}

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // ✅ 특정 경로는 Authorization 헤더와 관계없이 필터 제외
        return path.startsWith("/auth/login/kakao")
                || path.startsWith("/auth/test")
                || path.startsWith("/stocks/search")
                || path.startsWith("/stock/");
    }

}