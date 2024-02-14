package ru.sbrf.sber.finance.repositiries;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sbrf.sber.finance.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}