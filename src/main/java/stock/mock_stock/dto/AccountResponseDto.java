package stock.mock_stock.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccountResponseDto {
    private Long acid;
    private Long balance;
}
