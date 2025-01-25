package stock.mock_stock.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HealthCheckController {
    @GetMapping("/health")
    public void healthCheck() {}
}
