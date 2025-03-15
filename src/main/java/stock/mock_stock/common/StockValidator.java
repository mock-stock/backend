package stock.mock_stock.common;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import stock.mock_stock.entity.Stock;
import stock.mock_stock.exception.InvalidStockCodeException;

import stock.mock_stock.exception.NotFoundException;
import stock.mock_stock.repository.StockRepository;

@Component
@RequiredArgsConstructor
public class StockValidator {

    private final StockRepository stockRepository;

    public Stock getStock(String stckCode) {
        if(stckCode.length() != 6) throw new InvalidStockCodeException("Invalid stock code : " + stckCode);
        Stock stock = stockRepository.findByStockCode(stckCode);
        if(stock == null) throw new NotFoundException("Not Found stock code: " + stckCode);
        return stock;
    }
}
