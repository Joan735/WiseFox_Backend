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
import WiseFox.Finance.service.LedgerService;

import org.apache.commons.lang3.StringUtils;

@RestController
@RequestMapping("/api/ledgers") // from url starting with
public class LedgerController {
	@Autowired
	private LedgerService ledgerService;

	// Get all Ledgers
	// GET /api/ledgers/user/{user_id}
	@GetMapping("user/{user_id}")
	public ResponseEntity<Iterable<Ledger>> getAllLedgers(@PathVariable Long user_id) {
		Iterable<Ledger> ledgers = ledgerService.getMyLedgers(user_id);
		if (ledgers == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(ledgers);
	}
	
	// Get Ledger by ID
	// GET /api/ledgers/{id}
	@GetMapping("/{id}")
	public ResponseEntity<Optional<Ledger>> getLedgerById(@PathVariable Long id) {
		Optional<Ledger> ledger = ledgerService.getById(id);
		if (!ledger.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(ledger);
	}

	// Create Ledger
	// POST /api/ledgers/create
	@PostMapping("/create")
	public ResponseEntity<Ledger> createLedger(@RequestBody Ledger ledger) {
		try {
			// Basic validation
			if (StringUtils.isAnyBlank(ledger.getName(), ledger.getCurrency(), ledger.getUser().getName())) {
		        System.err.println("Enter all the data");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			Ledger createdLedger = ledgerService.create(ledger);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdLedger);
		} catch (Exception e) {
			System.err.println("Error:" + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// Update Ledger by ID
	// PUT /api/ledgers/{id}
	@PutMapping("/{id}")
	public ResponseEntity<Ledger> updateLedger(@PathVariable Long id, @RequestBody Ledger ledger) {
		// Verify that the data submitted is correct
		if (StringUtils.isAnyBlank(ledger.getName(), ledger.getCurrency(), ledger.getUser().getName())) {
	        System.err.println("Enter all the data");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		Ledger updatedLedger = ledgerService.update(id, ledger);
		if (updatedLedger == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(updatedLedger);
	}

	// Delete Ledger by ID
	// DELETE /api/ledgers/{id}
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteLedger(@PathVariable Long id) {
		boolean deleted = ledgerService.delete(id);
		if (!deleted) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok().build();
	}
}