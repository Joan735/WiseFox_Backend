package WiseFox.Finance.repository;

import WiseFox.Finance.model.Ledger;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LedgerRepository extends CrudRepository<Ledger, Long> {
    Optional<List<Ledger>> findByUserId(Long user_id);
    boolean existsById(Long id);
}