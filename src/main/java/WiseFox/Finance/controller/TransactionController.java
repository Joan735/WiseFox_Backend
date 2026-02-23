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

import WiseFox.Finance.model.Transaction;
import WiseFox.Finance.model.User;
import org.apache.commons.lang3.StringUtils;

@RestController
@RequestMapping("/api/ledger/{ledgerId}/transaction") // from url starting with
public class TransactionController {
	// @Autowired
	// private TransactionService transactionService;

	// Get Transactions
	// GET /api/ledger/{ledgerId}/transactions
	/*
	@GetMapping
	public ResponseEntity<Iterable<Transaction>> getAllTransactions(@PathVariable Long ledgerId, User user) {
		return ResponseEntity.ok(transactionService.getAll(ledgerId, user.getName()));
	}
	*/
	
	// Create Transaction
	// POST /api/ledger/{ledgerId}/transactions
	/*
	@PostMapping
	public ResponseEntity<?> create(@PathVariable Long ledgerId, User user) {
		return ResponseEntity.ok(transactionService.create(ledgerId, user.getName()));
	}
	*/
	
	// Delete Transaction
	// DELETE /api/ledger/{ledgerId}/transactions/{transactionId}
	@DeleteMapping("/{transactionId}")
	public ResponseEntity<?> delete(@PathVariable Long ledgerId, @PathVariable Long transactionId, User user) {
		boolean deleted = false; // transactionService.delete(transactionId, user.getName());
		if (!deleted) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok().build();
		
	}

}