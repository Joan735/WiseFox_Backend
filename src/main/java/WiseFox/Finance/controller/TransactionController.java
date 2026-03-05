package WiseFox.Finance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.model.Transaction;
import WiseFox.Finance.service.TransactionService;

@RestController
@RequestMapping("/api/transactions") // from url starting with
public class TransactionController {
	@Autowired
	private TransactionService transactionService;

	// Get All Transactions
	// GET /api/transactions/{ledgerId}
	@GetMapping("/{ledgerId}")
	public ResponseEntity<List<Transaction>> getAllTransactions(@PathVariable Long ledgerId) {
		try {
			List<Transaction> transactions = transactionService.getAll(ledgerId);
			return ResponseEntity.ok(transactions);
		} catch (ResponseStatusException e) {
			System.err.println("Error Get all: " + e);
			return ResponseEntity.status(e.getStatusCode()).build();
		}
	}

	// Create Transaction
	// POST /api/transactions/create
	@PostMapping("/create")
	public ResponseEntity<Transaction> create(@RequestBody Transaction transaction) {
		// Basic validation
		if (transaction.getAmount() == null || transaction.getDate() == null || transaction.getType() == null
				|| transaction.getLedger() == null) {
			System.err.println("Error: Enter all the data");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		try {
			Transaction createdTransaction = transactionService.create(transaction);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
		} catch (ResponseStatusException e) {
			System.err.println("Error Create: " + e);
			return ResponseEntity.status(e.getStatusCode()).build();
		}
	}

	// Delete Transaction
	// DELETE /api/transactions/delete/{transactionId}
	@DeleteMapping("/delete/{transactionId}")
	public ResponseEntity<Void> delete(@PathVariable Long transactionId) {
		try {
			transactionService.delete(transactionId);
			return ResponseEntity.ok().build();
		} catch (ResponseStatusException e) {
			System.err.println("Error Delete: " + e);
			return ResponseEntity.status(e.getStatusCode()).build();
		}
	}
}