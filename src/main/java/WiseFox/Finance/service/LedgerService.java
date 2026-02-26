package WiseFox.Finance.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.repository.LedgerRepository;

@Service
public class LedgerService {
	@Autowired
	private LedgerRepository ledgerRepository;

	// GET MY LEDGERS
	public List<Ledger> getMyLedgers(String name) {
		return ledgerRepository.findByOwner(name);
	}

	// GET BY ID
	public Optional<Ledger> getById(Long id) {
		return ledgerRepository.findById(id);
	}

	// CREATE
	public Ledger create(Ledger ledger) {
		return ledgerRepository.save(ledger);
	}
	
	// UPDATE
	public Ledger update(Long id, Ledger ledgerDetails) {
		return ledgerRepository.findById(id).map(ledger -> {
			ledger.setName(ledgerDetails.getName());
			ledger.setCurrency(ledgerDetails.getCurrency());
			ledger.setDescription(ledgerDetails.getDescription());
			return ledgerRepository.save(ledger);
		}).orElse(null);
	}

	// DELETE
	public boolean delete(Long id) {
		if (ledgerRepository.existsById(id)) {
			ledgerRepository.deleteById(id);
			return true;
		}
		return false;
	}
}