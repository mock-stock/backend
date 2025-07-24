package stock.mock_stock.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import stock.mock_stock.dto.StockInfoDto;
import stock.mock_stock.dto.WatchlistResponseDto;
import stock.mock_stock.entity.SearchHistory;
import stock.mock_stock.entity.Stock;
import stock.mock_stock.entity.User;
import stock.mock_stock.entity.Watchlist;
import stock.mock_stock.repository.StockRepository;
import stock.mock_stock.repository.UserRepository;
import stock.mock_stock.repository.WatchlistRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchlistServiceImpl implements WatchlistService{
    private final WatchlistRepository watchlistRepository;
    private final StockDetailService stockDetailService;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final Object apiLock = new Object(); // 동기화를 위한 Lock 객체

    @Override
    public List<WatchlistResponseDto> getWatchList(Long uid)  {
        // TODO: 도메인 주도 설계 방식으로 수정 할것
        List<Watchlist> watchlist = watchlistRepository.findByUserUid(uid);
        return watchlist.stream().map(item -> {
            StockInfoDto stockData = stockDetailService.getStockInfo(item.getStckCode());
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

    @Override
    public void addWatchlist(Long uid, Long sid) {
        Stock stock = stockRepository.findById(sid)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found"));

        User user = userRepository.findById(uid) // ✅ User 객체 조회
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (watchlistRepository.existsByUserUidAndSid(uid, sid)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 관심 종목에 추가된 주식입니다.");
        }

        Watchlist watchlist = Watchlist.builder()
                .user(user)
                .sid(sid)
                .stckCode(stock.getStckCode())
                .build();
        watchlistRepository.save(watchlist);
    }

    @Override
    public void deleteWatchList(Long uid, Long wid) {
        Watchlist watchlist = watchlistRepository.findById(wid)
                .orElseThrow(() -> new EntityNotFoundException("Search history with ID " + wid + " not found"));

        if (!watchlist.getUser().getUid().equals(uid)) {
            throw new AccessDeniedException("You are not authorized to delete this");
        }
        watchlistRepository.deleteById(wid);
    }
}
