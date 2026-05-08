package WiseFox.Finance.repository;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.User;
import WiseFox.Finance.model.UserLedger;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserLedgerRepository extends CrudRepository<UserLedger, Long> {

    boolean existsByUserAndLedger(User user, Ledger ledger);
    List<UserLedger> findByLedger(Ledger ledger);
    int countByLedger(Ledger ledger);
    @Modifying
    @Transactional
    void deleteByUserAndLedger(User user, Ledger ledger);

    // Used when deleting a ledger — removes all members/owners of that ledger
    @Modifying
    @Transactional
    void deleteByLedger(Ledger ledger);
}