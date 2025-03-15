// 📁 stock/mock_stock/security/handler/CustomAuthenticationEntryPoint.java

package stock.mock_stock.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 🔹 서버 내부 오류(500)인지 확인
        Throwable cause = authException.getCause();
        if (cause instanceof RuntimeException) {  // 내부에서 발생한 서버 오류라면 500 반환
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Internal Server Error\", \"message\":\"서버 내부 오류가 발생했습니다.\"}");
            return;
        }


        // ✅ 401 상태 코드 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // ✅ JSON 응답으로 반환
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().write("{\"error\":\"Unauthorized - 401\", \"message\":\"인증이 필요합니다.\"}");
    }
}