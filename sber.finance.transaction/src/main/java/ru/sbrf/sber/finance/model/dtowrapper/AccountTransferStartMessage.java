package ru.sbrf.sber.finance.model.dtowrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
public class AccountTransferStartMessage {
    private Long account_id_from;
    private Long account_id_to;
    private BigDecimal amount;
}