package ru.sbrf.sber.finance.repositiries;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sbrf.sber.finance.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
