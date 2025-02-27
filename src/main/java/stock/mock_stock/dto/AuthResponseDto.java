package stock.mock_stock.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class AuthResponseDto {

    @NotNull
    private String accessToken;
}
