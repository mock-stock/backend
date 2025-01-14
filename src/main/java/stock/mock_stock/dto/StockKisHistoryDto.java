package stock.mock_stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StockKisHistoryDto {

    private String stckName;

    @JsonProperty("stck_bsop_date")
    private String stckBsopDate;

    @JsonProperty("stck_oprc")
    private Long stckOprc;

    @JsonProperty("stck_clpr")
    private Long stckClpr;

    @JsonProperty("stck_hgpr")
    private Long stckHgpr;

    @JsonProperty("stck_lwpr")
    private Long stckLwpr;

    @JsonProperty("acml_vol")
    private Long acmlVol;

    @JsonProperty("prdy_vrss")
    private Long prdyVrss;

    @JsonProperty("stck_change_rate")
    private Double stckChangeRate;

}
