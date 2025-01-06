package stock.mock_stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebApprovalKey {

    @JsonProperty("approval_key")
    private String approvalKey;

}
