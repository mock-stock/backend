package stock.mock_stock.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Stock {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stckName;
    private String stckNameEng;
    private String stckNameAbr;

    @Column(length = 12)
    private String stckCode;
    @Column(length = 12)
    private String stckCodeStand;
    private String marketType;
    @Column(length = 12)
    private String securityType;
    private LocalDate listingDate;
    private Long listedShareQty;
    private Long faceValue;

}
