package WiseFox.Finance.repository;

import WiseFox.Finance.model.Transaction;
import WiseFox.Finance.model.Ledger;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findByLedgerId(Long ledgerId);
}