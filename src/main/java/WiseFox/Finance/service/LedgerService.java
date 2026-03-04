package WiseFox.Finance.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.repository.LedgerRepository;

@Service
public class LedgerService {
	@Autowired
	private LedgerRepository ledgerRepository;

	// GET MY LEDGERS
	public List<Ledger> getMyLedgers(Long user_id) {
		List<Ledger> ledgers = ledgerRepository.findByUserId(user_id).orElse(null);
		if (ledgers == null || ledgers.isEmpty()) {
			return null;
		}
		return ledgers;
	}

	// GET BY ID
	public Optional<Ledger> getById(Long id) {
		return ledgerRepository.findById(id);
	}

	// CREATE
	public Ledger create(Ledger ledger) {
		if (!ledgerRepository.existsByUser(ledger.getUser())) {
			System.err.println("ERROR: User doesn't exist.");
			throw new ResponseStatusException(HttpStatus.CONFLICT, "The ledger's user doesn't exist.");
		}
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