package stock.mock_stock.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponseDto {
    private Long userId;
    private String nickname;
    private String email;
    private List<UserWatchlistResponseDto> watchlist;

    public UserResponseDto(Long userId, String nickname, String email, List<UserWatchlistResponseDto> watchlist) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.watchlist = watchlist;
    }

    public Long getUserId() { return userId; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public List<UserWatchlistResponseDto> getWatchlist() { return watchlist; }
}


