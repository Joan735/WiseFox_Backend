package WiseFox.Finance.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.model.Transaction;
import WiseFox.Finance.repository.LedgerRepository;
import WiseFox.Finance.repository.TransactionRepository;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private LedgerRepository ledgerRepository;

	// GET ALL — returns empty list if ledger has no transactions
	public List<Transaction> getAll(Long ledgerId) {
		return transactionRepository.findByLedgerId(ledgerId)
				.orElse(Collections.emptyList());
	}

	// CREATE
	@Transactional
	public Transaction create(Transaction transaction) {
		if (transaction.getLedger() == null || transaction.getLedger().getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The transaction's ledger is required.");
		}
		if (!ledgerRepository.existsById(transaction.getLedger().getId())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The transaction's ledger doesn't exist.");
		}
		return transactionRepository.save(transaction);
	}

	// DELETE
	@Transactional
	public void delete(Long transactionId) {
		if (!transactionRepository.existsById(transactionId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with ID: " + transactionId);
		}
		transactionRepository.deleteById(transactionId);
	}
}