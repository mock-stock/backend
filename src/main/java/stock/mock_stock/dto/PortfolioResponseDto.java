package stock.mock_stock.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class PortfolioResponseDto {
    private Long pfid;
    private String stckCode;
    private String stckName;
    private Long stckQty;
    private BigDecimal avgPurchasePrice;
    private Long stckEvaluatedAmount;
    private Long evaluatedDiffAmount;
    private BigDecimal evaluatedDiffPercent;
    private Long totalInitialAmount;
}
