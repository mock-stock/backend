package stock.mock_stock.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccountResponseDto {
    @NotNull
    private Long acid;
    @NotNull
    private Long balance;
}
