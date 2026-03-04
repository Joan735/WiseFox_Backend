package WiseFox.Finance.repository;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
	Optional<List<Transaction>> findByLedgerId(Long ledgerId);
	boolean existsByLedger(Ledger ledger);
}