package WiseFox.Finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.repository.LedgerRepository;
import WiseFox.Finance.repository.UserRepository;

@Service
public class LedgerService {
	@Autowired
	private LedgerRepository ledgerRepository;
	@Autowired
	private UserRepository userRepository;

	// GET MY LEDGERS
	public List<Ledger> getMyLedgers(Long user_id) {
	    return ledgerRepository.findByUserId(user_id)
	        .filter(list -> !list.isEmpty())
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No ledgers found for user ID: " + user_id));
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
		if (!userRepository.existsById(ledger.getUser().getId())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The ledger's user doesn't exist.");
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

	// DELETE
	@Transactional
	public void delete(Long id) {
	    if (!ledgerRepository.existsById(id)) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ledger not found with ID: " + id);
	    }
	    ledgerRepository.deleteById(id);
	}
}