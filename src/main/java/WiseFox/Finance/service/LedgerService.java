package WiseFox.Finance.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.User;
import WiseFox.Finance.repository.LedgerRepository;
import WiseFox.Finance.repository.UserLedgerRepository;

@Service
public class LedgerService {
    @Autowired
    private LedgerRepository ledgerRepository;
    @Autowired
    private UserLedgerRepository userLedgerRepository;

    public Ledger create(Ledger ledger) { return ledgerRepository.save(ledger); }

    public Optional<Ledger> getById(Long id) { return ledgerRepository.findById(id); }

    public List<Ledger> getMyLedgers(User user) {
        return ledgerRepository.findByOwner(user);
    }

    public boolean delete(Long id) {
        if (ledgerRepository.existsById(id)) {
            ledgerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}