package stock.mock_stock.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import stock.mock_stock.entity.OrderType;

@Data
public class OrderRequestDto {

    @Enumerated(EnumType.STRING)
    private OrderType orderType;
    private String stckCode;
    private Long price;
    private Long quantity;
}
