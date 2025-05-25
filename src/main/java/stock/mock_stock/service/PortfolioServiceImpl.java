package stock.mock_stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stock.mock_stock.common.StockNameCache;
import stock.mock_stock.dto.PortfolioResponseDto;
import stock.mock_stock.dto.StockInfoOutput;
import stock.mock_stock.entity.Portfolio;
import stock.mock_stock.repository.PortfolioRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService{
    private final PortfolioRepository portfolioRepository;
    private final KISApiService kisApiService;
    private final StockNameCache stockNameCache;
    @Override
    public List<PortfolioResponseDto> getPortfolioWithEvaluation(Long uid) {
        List<Portfolio> existingPortfolios = portfolioRepository.findByUserUid(uid);

        return existingPortfolios.stream().map((portfolio -> {
            StockInfoOutput result = kisApiService.getDomesticStockInfo("J", portfolio.getStckCode());
            Long stockCurrentPrice = result.getStockKisDto().getStckPrpr();// 해당종목 현시세
            BigDecimal avgPurchasePrice = portfolio.getAvgPurchasePrice();
            String stockName = stockNameCache.getStockNameAndSid(portfolio.getStckCode()).getStckName();

            Long totalInitialAmount = avgPurchasePrice // NOTE: 총 투자원금
                    .multiply(BigDecimal.valueOf(portfolio.getStckQty()))
                    .longValue();
            Long stckEvaluatedAmount =stockCurrentPrice * portfolio.getStckQty(); // NOTE: 총 평가금액
            Long evaluatedDiffAmount = stckEvaluatedAmount - totalInitialAmount; // NOTE: 시세차익 금액


            BigDecimal decimalCurrentPrice = BigDecimal.valueOf(stockCurrentPrice);

            BigDecimal diff = decimalCurrentPrice.subtract(avgPurchasePrice); // NOTE: 시세 차이
            BigDecimal profitPercent = diff.divide(avgPurchasePrice, 10, RoundingMode.HALF_UP) // NOTE: 시세차익 퍼센트
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);

            return PortfolioResponseDto.builder()
                    .pfid(portfolio.getPfid())
                    .stckCode(portfolio.getStckCode())
                    .stckName(stockName)
                    .avgPurchasePrice(portfolio.getAvgPurchasePrice())
                    .stckQty(portfolio.getStckQty())
                    .totalInitialAmount(totalInitialAmount)
                    .stckEvaluatedAmount(stckEvaluatedAmount)
                    .evaluatedDiffAmount(evaluatedDiffAmount)
                    .evaluatedDiffPercent(profitPercent)
                    .build();
        })).collect(Collectors.toList());
    }
}
