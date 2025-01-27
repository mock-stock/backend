package stock.mock_stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StockKisHistoryDto {

    @NotNull
    private String stckName;

    @JsonProperty("array_index")
    @Schema(nullable = true)
    private Integer arrayIndex;

    @NotNull
    @JsonProperty("stck_bsop_date")
    private String stckBsopDate;

    @JsonProperty("stck_cntg_hour")
    @Schema(nullable = true)
    private String stckCntgHour;

    @JsonProperty("stck_prpr")
    @Schema(nullable = true)
    private Long stckPrpr;

    @NotNull
    @JsonProperty("stck_oprc")
    private Long stckOprc;

    @NotNull
    @JsonProperty("stck_clpr")
    private Long stckClpr;

    @NotNull
    @JsonProperty("stck_hgpr")
    private Long stckHgpr;

    @NotNull
    @JsonProperty("stck_lwpr")
    private Long stckLwpr;

    @NotNull
    @JsonProperty("acml_vol")
    private Long acmlVol;


    @JsonProperty("cntg_vol")
    @Schema(nullable = true)
    private Long cntgVol;

    @NotNull
    @JsonProperty("prdy_vrss")
    private Long prdyVrss;

    @NotNull
    @JsonProperty("stck_change_rate")
    private Double stckChangeRate;

}
