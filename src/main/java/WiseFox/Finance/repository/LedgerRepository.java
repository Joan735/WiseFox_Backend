package WiseFox.Finance.repository;

import WiseFox.Finance.model.Ledger;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LedgerRepository extends CrudRepository<Ledger, Long> {

    /**
     * Returns every ledger the given user has access to, regardless of role
     * (OWNER or MEMBER). The query joins through the user_ledger table so
     * shared ledgers show up as well, not only the ones the user created.
     *
     * Kept the Optional<List<Ledger>> return shape to stay binary-compatible
     * with existing callers (LedgerService, MonthlyReportService).
     */
    @Query("""
            SELECT DISTINCT l
              FROM Ledger l
              JOIN UserLedger ul ON ul.ledger = l
             WHERE ul.user.id = :userId
            """)
    Optional<List<Ledger>> findByUserId(@Param("userId") Long userId);
}