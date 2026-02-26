package WiseFox.Finance.repository;

import WiseFox.Finance.model.UserLedger;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLedgerRepository extends CrudRepository<UserLedger, Long> {

}