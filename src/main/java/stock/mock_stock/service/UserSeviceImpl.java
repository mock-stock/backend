package stock.mock_stock.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stock.mock_stock.dto.UserResponseDto;
import stock.mock_stock.dto.UserWatchlistResponseDto;
import stock.mock_stock.entity.User;
import stock.mock_stock.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSeviceImpl implements UserService {
    // TODO: 도메인 주도 설계 방식으로 수정 할것
    private final UserRepository userRepository;

    @Override
    public UserResponseDto getUserWithWatchlist(Long userId) {
        // 1. User 엔티티를 가져옴 (fetch join 사용으로 한 번의 SELECT 실행)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 2. Watchlist 엔티티를 DTO로 변환
        List<UserWatchlistResponseDto> userWatchlistResponse = user.getWatchlist().stream()
                .map(w -> UserWatchlistResponseDto.builder()
                        .wid(w.getWid())
                        .stockCode(w.getStckCode())
                        .build()
                )
                .collect(Collectors.toList());

        // 3. UserResponseDto로 변환 후 반환
        return UserResponseDto.builder()
                .userId(user.getUid())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .watchlist(userWatchlistResponse)
                .build();
    }
}
