package ru.sbrf.sber.finance.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sbrf.sber.finance.model.dtowrapper.Status;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;

    private BigDecimal amount;

    private String currency;

    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date date;

    private String account_period;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private Status status;
}