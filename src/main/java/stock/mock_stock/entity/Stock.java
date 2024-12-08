package stock.mock_stock.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Stock {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid;

    @Column(nullable = false)
    private String stckName;
    @Column(nullable = false)
    private String stckNameEng;
    @Column(nullable = false)
    private String stckNameAbr;

    @Column(length = 12, nullable = false)
    private String stckCode;
    @Column(length = 12, nullable = false)
    private String stckCodeStand;
    @Column(nullable = false)
    private String marketType;
    @Column(length = 12, nullable = false)
    private String securityType;
    @Column(nullable = false)
    private LocalDate listingDate;
    @Column(nullable = false)
    private Long listedShareQty;
    @Column(nullable = false)
    private Long faceValue;

}
