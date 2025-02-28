package stock.mock_stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stock.mock_stock.dto.StockInfoDto;
import stock.mock_stock.dto.WatchlistResponseDto;
import stock.mock_stock.entity.Watchlist;
import stock.mock_stock.repository.WatchlistRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchlistServiceImpl implements WatchlistService{
    private final WatchlistRepository watchlistRepository;
    private final StockDetailService stockDetailService;
    @Override
    public List<WatchlistResponseDto> getWatchList(Long uid) {
        List<Watchlist> watchlist = watchlistRepository.findByUid(uid);
        return watchlist.stream().map(item -> {
            StockInfoDto stockData = stockDetailService.getStockInfo(item.getStckCode()); // ✅ `stckCode`로 조회
            return WatchlistResponseDto.builder()
                    .wid(item.getWid())
                    .sid(item.getSid())
                    .stckName(stockData.getStckName())
                    .stckCode(stockData.getStckCode())
                    .stckCurPrice(stockData.getStckCurPrice())
                    .stckPrevClsDiffPrice(stockData.getStckPrevClsDiffPrice())
                    .stckPrevClsDiffPercent(stockData.getStckPrevClsDiffPercent())
                    .build();
        }).collect(Collectors.toList());


    }
}
