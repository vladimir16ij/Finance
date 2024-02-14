package ru.sbrf.sber.finance.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.sbrf.sber.finance.model.*;
import ru.sbrf.sber.finance.model.dtowrapper.AccountTransferStartMessage;
import ru.sbrf.sber.finance.model.dtowrapper.FinishMessage;
import ru.sbrf.sber.finance.model.dtowrapper.Status;
import ru.sbrf.sber.finance.model.dtowrapper.TransferResultMessage;
import ru.sbrf.sber.finance.repositiries.TransactionRepository;

import java.math.BigDecimal;

@Component
public class TransactionService {

    @Autowired
    private KafkaTemplate<String, TransferResultMessage> kafkaTemplate;

    @Autowired
    private TransactionRepository transactionRepository;
    @Transactional
    public void doTransferBegin(AccountTransferStartMessage message) {
        //нужно добавить будет account period
        Transaction transactionFrom = createTransaction(message.getAccount_id_from(), message.getAmount(),
                "", TransactionType.TRANSFER, Status.PENDING);
        Transaction transactionTo = createTransaction(message.getAccount_id_to(), message.getAmount(),
                "", TransactionType.TRANSFER, Status.PENDING);
        try{
            transactionRepository.save(transactionFrom);
            transactionRepository.save(transactionTo);
        } catch (DataIntegrityViolationException e) {
            //логируем
            TransferResultMessage messageResult = new TransferResultMessage(transactionFrom.getId(), transactionTo.getId(),
                    Status.FAILED, message.getAmount(), message.getAccount_id_from(), message.getAccount_id_to());
            kafkaTemplate.send("transaction-start-callback-topic", messageResult);
        } catch (DataAccessException e) {
            //логируем
            TransferResultMessage messageResult = new TransferResultMessage(transactionFrom.getId(), transactionTo.getId(),
                    Status.FAILED, message.getAmount(), message.getAccount_id_from(), message.getAccount_id_to());
            kafkaTemplate.send("transaction-start-callback-topic", messageResult);
        }
        TransferResultMessage messageResult = new TransferResultMessage(transactionFrom.getId(), transactionTo.getId(),
                Status.SUCCESS, message.getAmount(), message.getAccount_id_from(), message.getAccount_id_to());
        kafkaTemplate.send("transaction-start-callback-topic", messageResult);
    }

    @Transactional
    public void doTransferFinish(FinishMessage message) {
        Transaction transFrom = transactionRepository.getById(message.getTransaction_id_from());
        Transaction transTo = transactionRepository.getById(message.getTransaction_id_to());
        //ставим status транзакций Success or Failed
        transFrom.setStatus(message.getStatus());
        transTo.setStatus(message.getStatus());
    }

    public Transaction createTransaction(Long accountId, BigDecimal amount, String accountPeriod, TransactionType type,
                                         Status status) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setAccount_period(accountPeriod);
        transaction.setType(type);
        transaction.setStatus(status);
        return transaction;
    }

}
