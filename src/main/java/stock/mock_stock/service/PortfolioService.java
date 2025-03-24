package stock.mock_stock.service;

import stock.mock_stock.dto.PortfolioResponseDto;

import java.util.List;

public interface PortfolioService {
    public List<PortfolioResponseDto> getPortfolioWithEvaluation(Long uid);
}
