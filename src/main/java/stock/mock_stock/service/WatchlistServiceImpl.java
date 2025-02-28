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
import stock.mock_stock.entity.Watchlist;
import stock.mock_stock.repository.StockRepository;
import stock.mock_stock.repository.WatchlistRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchlistServiceImpl implements WatchlistService{
    private final WatchlistRepository watchlistRepository;
    private final StockDetailService stockDetailService;
    private final StockRepository stockRepository;
    private final Object apiLock = new Object(); // 동기화를 위한 Lock 객체

    @Override
    public List<WatchlistResponseDto> getWatchList(Long uid)  {
        List<Watchlist> watchlist = watchlistRepository.findByUid(uid);
        System.out.println("watchlist = " + watchlist);
        return watchlist.stream().map(item -> {
            synchronized (apiLock){ // NOTE: 배치, 캐싱으로도 파라미터가 다를땐 처리할수없기떄문에 동기화로 단일 쓰레드로 요청사이에 0.5초를 주워 에러발생하지않도록 처리, 하지만 100명이 동시에 요청했다면 0.5*100 =50초 딜레이 발생
                // API 호출 전 대기 시간 추가 (기존 호출과 겹치지 않도록)
            StockInfoDto stockData = stockDetailService.getStockInfo(item.getStckCode());
                try {
                    Thread.sleep(500); // 초당요청건수땜에 추가
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return WatchlistResponseDto.builder()
                    .wid(item.getWid())
                    .sid(item.getSid())
                    .stckName(stockData.getStckName())
                    .stckCode(stockData.getStckCode())
                    .stckCurPrice(stockData.getStckCurPrice())
                    .stckPrevClsDiffPrice(stockData.getStckPrevClsDiffPrice())
                    .stckPrevClsDiffPercent(stockData.getStckPrevClsDiffPercent())
                    .build();
        }}).collect(Collectors.toList());


    }

    @Override
    public void addWatchlist(Long uid, Long sid) {
        Stock stock = stockRepository.findById(sid)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found"));

        if (watchlistRepository.existsByUidAndSid(uid, sid)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 관심 종목에 추가된 주식입니다.");
        }

        Watchlist watchlist = Watchlist.builder()
                .uid(uid)
                .sid(sid)
                .stckCode(stock.getStckCode())
                .build();
        watchlistRepository.save(watchlist);
    }

    @Override
    public void deleteWatchList(Long uid, Long wid) {
        Watchlist watchlist = watchlistRepository.findById(wid)
                .orElseThrow(() -> new EntityNotFoundException("Search history with ID " + wid + " not found"));

        if (!watchlist.getUid().equals(uid)) {
            throw new AccessDeniedException("You are not authorized to delete this");
        }
        watchlistRepository.deleteById(wid);
    }
}
