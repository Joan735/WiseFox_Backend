package WiseFox.Finance.controller;

import java.util.List;

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
import org.springframework.web.server.ResponseStatusException;

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
	public ResponseEntity<List<Ledger>> getAllLedgers(@PathVariable Long user_id) {
		try {
			List<Ledger> ledgers = ledgerService.getMyLedgers(user_id);
			return ResponseEntity.ok(ledgers);
		} catch (ResponseStatusException e) {
			System.err.println("Error Get all: " + e);
			return ResponseEntity.status(e.getStatusCode()).build();
		}
	}

	// Get Ledger by ID
	// GET /api/ledgers/{id}
	@GetMapping("/{id}")
	public ResponseEntity<Ledger> getLedgerById(@PathVariable Long id) {
		try {
			Ledger ledger = ledgerService.getById(id);
			return ResponseEntity.ok(ledger);
		} catch (ResponseStatusException e) {
			System.err.println("Error Get by id: " + e);
			return ResponseEntity.status(e.getStatusCode()).build();
		}
	}

	// Create Ledger
	// POST /api/ledgers/create
	@PostMapping("/create")
	public ResponseEntity<Ledger> createLedger(@RequestBody Ledger ledger) {
		try {
			// Basic validation
			if (StringUtils.isAnyBlank(ledger.getName(), ledger.getCurrency())) {
				System.err.println("Error: Enter all the data");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			Ledger createdLedger = ledgerService.create(ledger);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdLedger);
		} catch (ResponseStatusException e) {
			System.err.println("Error Create:" + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// Update Ledger by ID
	// PUT /api/ledgers/{id}
	@PutMapping("/{id}")
	public ResponseEntity<Ledger> updateLedger(@PathVariable Long id, @RequestBody Ledger ledger) {
		// Verify that the data submitted is correct
		if (StringUtils.isAnyBlank(ledger.getName(), ledger.getCurrency())) {
			System.err.println("Error: Enter all the data");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		try {
			Ledger updatedLedger = ledgerService.update(id, ledger);
			return ResponseEntity.ok(updatedLedger);
		} catch (ResponseStatusException e) {
			System.err.println("Error Update: " + e);
			return ResponseEntity.status(e.getStatusCode()).build();
		}
	}

	// Delete Ledger by ID
	// DELETE /api/ledgers/{id}
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteLedger(@PathVariable Long id) {
		try {
			ledgerService.delete(id);
			return ResponseEntity.ok().build();
		} catch (ResponseStatusException e) {
			System.err.println("Error Delete: " + e);
			return ResponseEntity.status(e.getStatusCode()).build();
		}
	}
}