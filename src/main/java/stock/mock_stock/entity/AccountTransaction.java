package stock.mock_stock.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AccountTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;
    private LocalDateTime transactionTime;

    @ManyToOne
    @JoinColumn(name = "acid")
    private Account account;
}
