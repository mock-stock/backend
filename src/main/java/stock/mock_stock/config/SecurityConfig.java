package stock.mock_stock.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import stock.mock_stock.security.CustomAuthenticationEntryPoint;
import stock.mock_stock.security.JwtTokenProvider;
import stock.mock_stock.security.filter.JwtAuthenticationFilter;
import stock.mock_stock.security.filter.JwtRefreshFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/stocks/search/history")
                        .authenticated()
                        .requestMatchers("/stocks/search/*",
                                "/auth/test",
                                "/auth/login/*",
                                "/stock/*",
                                "/stock/history/*",
                                "/auth/refresh")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.getWriter().write("{\"error\":\"Unauthorized access - 401\"}");
                        }))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
//                .addFilterBefore(new JwtRefreshFilter(jwtTokenProvider), JwtAuthenticationFilter.class);
return http.build();
    }
}
