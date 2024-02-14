package ru.sbrf.sber.finance.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import ru.sbrf.sber.finance.model.dtowrapper.AccountTransferStartMessage;
import ru.sbrf.sber.finance.model.dtowrapper.FinishMessage;
import ru.sbrf.sber.finance.services.TransactionService;


@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @KafkaListener(topics = "transaction-start-topic")
    public void receive(AccountTransferStartMessage message) {
        transactionService.doTransferBegin(message);
    }

    @KafkaListener(topics = "transaction-finish-topic")
    public void receiveCallback(FinishMessage message) {
        transactionService.doTransferFinish(message);
    }
}
