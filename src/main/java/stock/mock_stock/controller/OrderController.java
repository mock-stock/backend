package stock.mock_stock.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import stock.mock_stock.dto.OrderRequestDto;
import stock.mock_stock.dto.OrderResponseDto;
import stock.mock_stock.entity.OrderStatus;
import stock.mock_stock.entity.TradeActionType;
import stock.mock_stock.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping("/buy")
    public ResponseEntity<?> buyStock(@RequestBody OrderRequestDto orderRequestDto){
        System.out.println("OrderController.buyStock");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Claims claims) {
            Long userId = Long.valueOf(claims.getSubject());
            orderService.processOrder(userId,
                    orderRequestDto.getStckCode(),
                    orderRequestDto.getQuantity(),
                    orderRequestDto.getPrice(),
                    orderRequestDto.getOrderType(),
                    TradeActionType.BUY);


            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated"); // 401 반환
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellStock(@RequestBody OrderRequestDto orderRequestDto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Claims claims) {
            Long userId = Long.valueOf(claims.getSubject());

            orderService.processOrder(userId,
                    orderRequestDto.getStckCode(),
                    orderRequestDto.getQuantity(),
                    orderRequestDto.getPrice(),
                    orderRequestDto.getOrderType(),
                    TradeActionType.SELL);

            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated"); // 401 반환
    }

    @GetMapping("/orders")
    public List<OrderResponseDto> getOrders(@RequestParam(name= "stockCode", required = false) String stockCode,
                                            @RequestParam(name= "status", required = false) OrderStatus status){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Claims claims) {
            Long userId = Long.valueOf(claims.getSubject());
            return orderService.getOrders(userId, stockCode, status);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated"); // 401 반환
    }


}
