package stock.mock_stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockKisDto {

    // 실제 KIS에선 String이지만 자동으로 Long, Double등 형식만 맞으면 바꿔준다.
    @JsonProperty("stck_prpr")
    private Long stckPrpr;

    @JsonProperty("prdy_vrss")
    private Long prdyVrss;

    @JsonProperty("prdy_ctrt")
    private Double prdyCtrt;
    // 기본 생성자
    public StockKisDto() {
    }
    public StockKisDto(Long stckPrpr, Long prdyVrss, Double prdyCtrt){
        this.stckPrpr = stckPrpr;
        this.prdyVrss = prdyVrss;
        this.prdyCtrt = prdyCtrt;
    }
}
