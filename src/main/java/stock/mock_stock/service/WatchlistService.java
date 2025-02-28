package stock.mock_stock.service;

import stock.mock_stock.dto.WatchlistResponseDto;
import java.util.List;

public interface WatchlistService {

    public List<WatchlistResponseDto> getWatchList(Long uid);
    public void addWatchlist(Long uid, Long sid);
    public void deleteWatchList(Long uid, Long wid);
}
