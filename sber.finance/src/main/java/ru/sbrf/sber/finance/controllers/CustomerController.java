package ru.sbrf.sber.finance.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import ru.sbrf.sber.finance.model.Customer;
import ru.sbrf.sber.finance.model.dtowrapper.Status;
import ru.sbrf.sber.finance.model.dtowrapper.TransferResultMessage;
import ru.sbrf.sber.finance.repositiries.CustomerRepository;
import ru.sbrf.sber.finance.services.AccountService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    AccountService accountService;

    @GetMapping("/")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @PostMapping("/")
    public Customer addCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    @PostMapping("/")
    public void doTransfer(@RequestBody Long account_id_from, Long account_id_to, BigDecimal amount) {
        accountService.doTransfer(account_id_from, account_id_to, amount);
    }

    @KafkaListener(topics = "transaction-start-callback-topic")
    public void receive(TransferResultMessage message) {
        if(message.getStatus()==Status.SUCCESS){
            accountService.doTransferFinish(message);
        } else{
            //вывести сообщение об ошибке
        }
    }
}
