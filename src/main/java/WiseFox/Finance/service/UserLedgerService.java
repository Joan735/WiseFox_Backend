package WiseFox.Finance.service;

import WiseFox.Finance.repository.AuthRepository;
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
	@Autowired
	private AuthRepository authRepository;
	@Autowired
	private EmailService emailService;

	// ─────────────────────────────────────────────────────────────────────────
	// CREATE (owner)
	// ─────────────────────────────────────────────────────────────────────────
	@Transactional
	public UserLedger create(UserLedger userLedger) {
		userLedger.setPermission(Permission.OWNER);
		validateRefs(userLedger);
		if (userLedgerRepository.existsByUserAndLedger(userLedger.getUser(), userLedger.getLedger())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"The User with ID: " + userLedger.getUser().getId()
							+ ", has been already assigned to this Ledger with ID: "
							+ userLedger.getLedger().getId());
		}
		return userLedgerRepository.save(userLedger);
	}

	// ─────────────────────────────────────────────────────────────────────────
	// SHARE (member, by userId+ledgerId already known)
	// ─────────────────────────────────────────────────────────────────────────
	@Transactional
	public UserLedger share(UserLedger userLedger) {
		userLedger.setPermission(Permission.MEMBER);
		validateRefs(userLedger);
		if (userLedgerRepository.existsByUserAndLedger(userLedger.getUser(), userLedger.getLedger())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"The User with ID: " + userLedger.getUser().getId()
							+ ", has been already assigned to this Ledger with ID: "
							+ userLedger.getLedger().getId());
		}
		return userLedgerRepository.save(userLedger);
	}

	// ─────────────────────────────────────────────────────────────────────────
	// SHARE BY EMAIL
	// ─────────────────────────────────────────────────────────────────────────
	/**
	 * Shares a ledger with the user that owns the given email address.
	 *
	 * Flow:
	 *   1. Look up the recipient by email — 404 if no such user exists.
	 *   2. Look up the ledger and the owner.
	 *   3. Reject if the recipient is the owner themself, or already a member.
	 *   4. Insert the user_ledger row as MEMBER.
	 *   5. Send the notification email.
	 *
	 * Steps 4 and 5 share the same transaction. If the email send throws,
	 * the user_ledger insert is rolled back and the caller sees an error —
	 * matching the requested "all or nothing" behaviour.
	 */
	@Transactional
	public UserLedger shareByEmail(Long ownerUserId, Long ledgerId, String targetEmail) {
	    System.out.println("[shareByEmail] CALLED owner=" + ownerUserId + " ledger=" + ledgerId + " email=" + targetEmail);  // ← 加这一行

		// ── 1. Recipient lookup by email ─────────────────────────────────────
		String email = targetEmail == null ? "" : targetEmail.trim();
		if (email.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "targetEmail is required");
		}

		User recipient = authRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No user found with email: " + email));

		// ── 2. Owner & ledger lookup ─────────────────────────────────────────
		User owner = userRepository.findById(ownerUserId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Owner user not found"));

		Ledger ledger = ledgerRepository.findById(ledgerId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Ledger not found"));

		// ── 3. Sanity checks ─────────────────────────────────────────────────
		if (recipient.getId().equals(owner.getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"You cannot share a ledger with yourself");
		}
		if (userLedgerRepository.existsByUserAndLedger(recipient, ledger)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"This user is already a member of the ledger");
		}

		// ── 4. Insert as MEMBER ──────────────────────────────────────────────
		UserLedger membership = new UserLedger();
		membership.setUser(recipient);
		membership.setLedger(ledger);
		membership.setPermission(Permission.MEMBER);
		UserLedger saved = userLedgerRepository.save(membership);

		// ── 5. Notification email — runtime exception here will roll back the
		//      user_ledger insert above (same @Transactional scope).
		emailService.sendLedgerSharedNotification(
				recipient.getEmail(),
				recipient.getName(),
				owner.getName(),
				ledger.getName());

		return saved;
	}

	// ─────────────────────────────────────────────────────────────────────────
	// DELETE
	// ─────────────────────────────────────────────────────────────────────────
	@Transactional
	public void deleteByUserAndLedger(Long userId, Long ledgerId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
		Ledger ledger = ledgerRepository.findById(ledgerId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ledger no encontrado"));
		if (!userLedgerRepository.existsByUserAndLedger(user, ledger)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The User with ID: " + user.getId() + ", is not assigned to the Ledger with ID: "
							+ ledger.getId());
		}
		userLedgerRepository.deleteByUserAndLedger(user, ledger);
	}

	// ─────────────────────────────────────────────────────────────────────────
	// helpers
	// ─────────────────────────────────────────────────────────────────────────
	private void validateRefs(UserLedger userLedger) {
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
	}
}