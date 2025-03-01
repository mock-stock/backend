package stock.mock_stock.service;


import stock.mock_stock.dto.UserResponseDto;

public interface UserService {

    public UserResponseDto getUserWithWatchlist(Long userId);
}
