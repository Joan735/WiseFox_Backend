package WiseFox.Finance.controller;

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
	public ResponseEntity<Iterable<Transaction>> getAllTransactions(@PathVariable Long ledgerId) {
		Iterable<Transaction> transactions = transactionService.getAll(ledgerId);
		if (transactions == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(transactions);
	}

	// Create Transaction
	// POST /api/transactions/create
	@PostMapping("/create")
	public ResponseEntity<Transaction> create(@RequestBody Transaction transaction) {
		try {
			// Basic validation
			if (transaction.getAmount() == null || transaction.getDate() == null || transaction.getType() == null
					|| transaction.getLedger() == null || transaction.getLedger().getUser() == null) {
				System.err.println("Enter all the data");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			Transaction createdTransaction = transactionService.create(transaction);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);

		} catch (Exception e) {
			System.err.println("Error:" + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// Delete Transaction
	// DELETE /api/transactions/delete/{transactionId}
	@DeleteMapping("/delete/{transactionId}")
	public ResponseEntity<Void> delete(@PathVariable Long transactionId) {
		boolean deleted = transactionService.delete(transactionId);
		if (!deleted) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok().build();
	}
}