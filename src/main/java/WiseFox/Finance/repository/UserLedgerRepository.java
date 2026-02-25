package WiseFox.Finance.repository;

import WiseFox.Finance.model.UserLedger;
import WiseFox.Finance.model.User;
import WiseFox.Finance.model.Ledger;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserLedgerRepository extends CrudRepository<UserLedger, Long> {
    List<UserLedger> findByUser(User user);
    Optional<UserLedger> findByUserAndLedger(User user, Ledger ledger);
}