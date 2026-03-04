package WiseFox.Finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.model.Transaction;
import WiseFox.Finance.repository.TransactionRepository;

@Service
public class TransactionService {
	@Autowired
	private TransactionRepository transactionRepository;

	// Get all
	public List<Transaction> getAll(Long ledgerId) {
		List<Transaction> transactions = transactionRepository.findByLedgerId(ledgerId).orElse(null);
		if (transactions == null || transactions.isEmpty()) {
			return null;
		}
		return transactions;
	}

	// Create
	public Transaction create(Transaction transaction) {
		if (!transactionRepository.existsByLedger(transaction.getLedger())) {
			System.err.println("ERROR: Ledger doesn't exist.");
			throw new ResponseStatusException(HttpStatus.CONFLICT, "The transaction's ledger doesn't exist.");
		}
		return transactionRepository.save(transaction);
	}

	// Delete
	public boolean delete(Long transactionId) {
		if (transactionRepository.existsById(transactionId)) {
			transactionRepository.deleteById(transactionId);
			return true;
		}
		return false;
	}
}