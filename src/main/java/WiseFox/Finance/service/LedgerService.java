package WiseFox.Finance.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.repository.LedgerRepository;
import WiseFox.Finance.repository.TransactionRepository;
import WiseFox.Finance.repository.UserLedgerRepository;
import WiseFox.Finance.repository.UserRepository;

@Service
public class LedgerService {

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserLedgerRepository userLedgerRepository;

    // GET MY LEDGERS
    public List<Ledger> getMyLedgers(Long user_id) {
        return ledgerRepository.findByUserId(user_id)
                .orElse(Collections.emptyList());
    }

    // GET BY ID
    public Ledger getById(Long id) {
        return ledgerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ledger not found with ID: " + id));
    }

    // CREATE
    @Transactional
    public Ledger create(Ledger ledger) {
        if (ledger.getUser() == null || ledger.getUser().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ledger's User is required.");
        }
        return ledgerRepository.save(ledger);
    }

    // UPDATE
    @Transactional
    public Ledger update(Long id, Ledger ledgerDetails) {
        return ledgerRepository.findById(id).map(ledger -> {
            ledger.setName(ledgerDetails.getName());
            ledger.setCurrency(ledgerDetails.getCurrency());
            ledger.setDescription(ledgerDetails.getDescription());
            return ledgerRepository.save(ledger);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot update: Ledger not found"));
    }

    // DELETE — order matters: transaction → user_ledger → ledger
    @Transactional
    public void delete(Long id) {
        Ledger ledger = ledgerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ledger not found with ID: " + id));

        // 1. Delete all transactions for this ledger
        List<Long> transactionIds = transactionRepository.findByLedgerId(id)
                .orElse(Collections.emptyList())
                .stream()
                .map(t -> t.getId())
                .toList();
        if (!transactionIds.isEmpty()) {
            transactionRepository.deleteAllById(transactionIds);
        }

        // 2. Delete all user_ledger entries for this ledger
        userLedgerRepository.deleteByLedger(ledger);

        // 3. Now safe to delete the ledger itself
        ledgerRepository.delete(ledger);
    }
}