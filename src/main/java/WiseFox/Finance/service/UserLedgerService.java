package WiseFox.Finance.service;

import WiseFox.Finance.repository.LedgerRepository;
import WiseFox.Finance.repository.UserLedgerRepository;
import WiseFox.Finance.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.User;
import WiseFox.Finance.model.UserLedger;
import WiseFox.Finance.model.UserLedger.Permission;

@Service
public class UserLedgerService {
	@Autowired
	private UserLedgerRepository userLedgerRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LedgerRepository ledgerRepository;

	// CREATE
	@Transactional
	public UserLedger create(UserLedger userLedger) {
		userLedger.setPermission(Permission.OWNER);
		if (userLedger.getUser() == null || userLedger.getUser().getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserLedger's User is required.");
		}
		if (userLedger.getLedger() == null || userLedger.getLedger().getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserLedger's Ledger is required.");
		}
		if (!userRepository.existsById(userLedger.getUser().getId())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The UserLedger's user doesn't exist.");
		}
		if (!ledgerRepository.existsById(userLedger.getLedger().getId())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The UserLedger's ledger doesn't exist.");
		}
		if (userLedgerRepository.existsByUserAndLedger(userLedger.getUser(), userLedger.getLedger())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "The User with ID: " + userLedger.getUser().getId()
					+ ", has been already assigned to this Ledger with ID: " + userLedger.getLedger().getId());
		}
		return userLedgerRepository.save(userLedger);
	}

	// SHARE
	@Transactional
	public UserLedger share(UserLedger userLedger) {
		userLedger.setPermission(Permission.MEMBER);
		if (userLedger.getUser() == null || userLedger.getUser().getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserLedger's User is required.");
		}

		if (userLedger.getLedger() == null || userLedger.getLedger().getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserLedger's Ledger is required.");
		}
		if (!userRepository.existsById(userLedger.getUser().getId())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The UserLedger's user doesn't exist.");
		}
		if (!ledgerRepository.existsById(userLedger.getLedger().getId())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The UserLedger's ledger doesn't exist.");
		}
		if (userLedgerRepository.existsByUserAndLedger(userLedger.getUser(), userLedger.getLedger())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "The User with ID: " + userLedger.getUser().getId()
					+ ", has been already assigned to this Ledger with ID: " + userLedger.getLedger().getId());
		}
		return userLedgerRepository.save(userLedger);
	}

	// DELETE
	@Transactional
	public void deleteByUserAndLedger(Long userId, Long ledgerId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
		Ledger ledger = ledgerRepository.findById(ledgerId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ledger no encontrado"));
		if (!userLedgerRepository.existsByUserAndLedger(user, ledger)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The User with ID: " + user.getId() + ", is not assigned to the Ledger with ID: " + ledger.getId());
		}
		userLedgerRepository.deleteByUserAndLedger(user, ledger);
	}
}
