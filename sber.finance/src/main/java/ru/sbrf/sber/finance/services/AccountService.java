package ru.sbrf.sber.finance.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sbrf.sber.finance.model.*;
import ru.sbrf.sber.finance.model.dtowrapper.AccountTransferStartMessage;
import ru.sbrf.sber.finance.model.dtowrapper.FinishMessage;
import ru.sbrf.sber.finance.model.dtowrapper.Status;
import ru.sbrf.sber.finance.model.dtowrapper.TransferResultMessage;
import ru.sbrf.sber.finance.repositiries.AccountRepository;

import java.math.BigDecimal;
@Service
public class AccountService {

    @Autowired
    private KafkaTemplate<String, AccountTransferStartMessage> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, FinishMessage> kafkaFinishTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;
    @Transactional
    public AccountTransferStartMessage doTransfer(Long account_id_from, Long account_id_to, BigDecimal amount) {
        // проверить блокировки
        Account accountFrom = accountRepository.findById(account_id_from).
                orElseThrow(() -> new RuntimeException("Account not found. Id = " + account_id_from));
        Account accountTo = accountRepository.findById(account_id_to).
                orElseThrow(() -> new RuntimeException("Account not found. Id = " + account_id_to));
        if (accountFrom.getBalance().compareTo(amount) < 0) {
            //вывести: Недостаточно средств
        }
        AccountTransferStartMessage message = new AccountTransferStartMessage(account_id_from, account_id_to, amount);
        // логируем отправку
        kafkaTemplate.send("transaction-start-topic", message);
        return message;
    }

    @Transactional
    public void doTransferFinish(TransferResultMessage message) {
        Account accountFrom = accountRepository.findById(message.getAccount_id_from()).
                orElseThrow(() -> new RuntimeException("Account not found. Id = "+ message.getAccount_id_from()));
        Account accountTo = accountRepository.findById(message.getAccount_id_to()).
                orElseThrow(() -> new RuntimeException("Account not found. Id = "+ message.getAccount_id_to()));
        if (accountFrom.getBalance().compareTo(message.getAmount()) < 0) {
            //логируем
            //для сохранения транзакционности вызываем через бин
            accountService.doRollBack(message);
        } else {
            //логируем
            accountFrom.setBalance(accountFrom.getBalance().add(message.getAmount()));
            accountTo.setBalance(accountTo.getBalance().add(message.getAmount()));
            //для сохранения транзакционности вызываем через бин
            accountService.doFinishTransaction(message);
        }

    }
    @Transactional
    public void doFinishTransaction(TransferResultMessage message) {
        FinishMessage messageFinish = new FinishMessage(Status.SUCCESS, message.getTransaction_id_from(), message.getAccount_id_to());
        //логируем
        kafkaFinishTemplate.send("transaction-finish-topic", messageFinish);
    }

    @Transactional
    public void doRollBack(TransferResultMessage message) {
        FinishMessage messageFinish = new FinishMessage(Status.FAILED, message.getTransaction_id_from(), message.getAccount_id_to());
        //логируем
        kafkaFinishTemplate.send("transaction-finish-topic", messageFinish);
    }
}
