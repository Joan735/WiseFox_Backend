package WiseFox.Finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.model.UserLedger;
import WiseFox.Finance.service.UserLedgerService;

@RestController
@RequestMapping("/api/userledger") // from url starting with
public class UserLedgerController {
	@Autowired
	private UserLedgerService userledgerService;

	// Create user ledger
	// POST /api/userledger/create
	@PostMapping("/create")
	public ResponseEntity<UserLedger> createUserLedger(@RequestBody UserLedger userledger) {
		try {
			UserLedger createdUserLedger = userledgerService.create(userledger);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdUserLedger);
		} catch (ResponseStatusException e) {
			System.err.println("ERROR: " + e);
			return ResponseEntity.status(e.getStatusCode()).build();
		}
	}

	// Share Ledger
	// POST /api/userledger/share
	@PostMapping("/share")
	public ResponseEntity<UserLedger> shareLedger(@RequestBody UserLedger userledger) {
		try {
			UserLedger sharedUserLedger = userledgerService.share(userledger);
			return ResponseEntity.status(HttpStatus.CREATED).body(sharedUserLedger);
		} catch (ResponseStatusException e) {
			System.err.println("ERROR: " + e);
			return ResponseEntity.status(e.getStatusCode()).build();

		}
	}

	// Delete Ledger by ID
	// DELETE /api/userledger/delete
	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteLedger(@RequestParam Long userId, @RequestParam Long ledgerId) {
	    try {
	    	userledgerService.deleteByUserAndLedger(userId, ledgerId);	
	        return ResponseEntity.noContent().build();
	    } catch (ResponseStatusException e) {
			System.err.println("ERROR: " + e);
	        return ResponseEntity.status(e.getStatusCode()).build();
	    }
	}
}