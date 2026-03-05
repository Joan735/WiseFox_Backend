package WiseFox.Finance.repository;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.User;
import WiseFox.Finance.model.UserLedger;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserLedgerRepository extends CrudRepository<UserLedger, Long> {
	boolean existsByUserAndLedger(User user, Ledger ledger);
	@Modifying
	@Transactional		
	void deleteByUserAndLedger(User user, Ledger ledger);
}