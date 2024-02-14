package ru.sbrf.sber.finance.repositiries;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sbrf.sber.finance.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}