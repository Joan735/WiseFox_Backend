package WiseFox.Finance.repository;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
	Optional<List<Transaction>> findByLedgerId(Long ledgerId);
	boolean existsByLedger(Ledger ledger);
	
	@Query("SELECT t FROM Transaction t WHERE t.ledger.id = :ledgerId AND t.date >= :start AND t.date <= :end")
	List<Transaction> findByLedgerIdAndDateBetween(
	    @Param("ledgerId") Long ledgerId,
	    @Param("start") LocalDate start,
	    @Param("end") LocalDate end
	);
}

