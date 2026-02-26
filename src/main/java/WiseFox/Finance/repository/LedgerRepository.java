package WiseFox.Finance.repository;

import WiseFox.Finance.model.Ledger;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LedgerRepository extends CrudRepository<Ledger, Long> {
    List<Ledger> findByOwner(String owner);
}