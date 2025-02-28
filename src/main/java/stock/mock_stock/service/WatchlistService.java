package stock.mock_stock.service;

import stock.mock_stock.dto.WatchlistResponseDto;
import java.util.List;

public interface WatchlistService {

    public List<WatchlistResponseDto> getWatchList(Long uid);
}
