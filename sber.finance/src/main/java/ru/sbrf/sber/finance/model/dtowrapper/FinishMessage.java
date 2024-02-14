package ru.sbrf.sber.finance.model.dtowrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FinishMessage {
    private Status status;
    private Long transaction_id_from;
    private Long transaction_id_to;
}
