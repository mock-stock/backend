package stock.mock_stock.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
//                .allowCredentials(true) // Credentials 허용
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE", "")
                .exposedHeaders("Authorization");


    }
}
