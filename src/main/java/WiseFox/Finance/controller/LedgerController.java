package WiseFox.Finance.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.User;

import org.apache.commons.lang3.StringUtils;

@RestController
@RequestMapping("/api/ledgers") // from url starting with
public class LedgerController {
	// @Autowired
	// private LedgerService ledgerService;

	// Get all Ledgers
	// GET /api/ledgers
	/*
	@GetMapping
	public ResponseEntity<Iterable<Ledger>> getAllLedgers(User user) {
		return ResponseEntity.ok(ledgerService.getMyLedgers(user.getName()));
	}
    */
	
	// Get Ledger by ID
	// GET /api/ledgers/{id}
	@GetMapping("/{id}")
	public ResponseEntity<Optional<Ledger>> getLedgerById(@PathVariable Long id) {
		Optional<Ledger> ledger = null; // ledgerService.getById(id);
		if (ledger.isPresent()) {
			return ResponseEntity.ok(ledger);
		}
		return ResponseEntity.notFound().build();
	}

	// Create Ledger
	// POST /api/ledgers/create
	@PostMapping("/create")
	public ResponseEntity<Ledger> createLedger(@RequestBody Ledger ledger) {
		try {
			// Basic validation
			if (StringUtils.isAnyBlank(ledger.getName(), ledger.getCurrency(), ledger.getOwner().getName())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			Ledger createdLedger = null; // ledgerService.create(ledger);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdLedger);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// Update Ledger by ID
	// PUT /api/ledgers/{id}
	@PutMapping("/{id}")
	public ResponseEntity<Ledger> updateLedger(@PathVariable Long id, @RequestBody Ledger ledger) {
		// Verify that the data submitted is correct
		if (StringUtils.isAnyBlank(ledger.getName(), ledger.getCurrency(), ledger.getOwner().getName())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		Ledger updatedLedger = null; // ledgerService.update(id, nurse);
		if (updatedLedger == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(updatedLedger);
	}

	// Delete Ledger by ID
	// DELETE /api/ledgers/{id}
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteLedger(@PathVariable Long id) {
		boolean deleted = false; // ledgerService.delete(id);
		if (!deleted) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok().build();
	}

	// Share Ledger
	// POST /api/ledgers/{id}/share
	/*
	@PostMapping("/{id}/share")
	public ResponseEntity<?> shareLedger(@PathVariable Long ledgerId, User user) {
		return ResponseEntity.ok(ledgerService.share(ledgerId, user.getName()));
	}
	*/

}