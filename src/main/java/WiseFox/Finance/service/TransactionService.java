package WiseFox.Finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import WiseFox.Finance.model.Transaction;
import WiseFox.Finance.repository.TransactionRepository;

@Service
public class TransactionService {
	@Autowired
	private TransactionRepository transactionRepository;

	public List<Transaction> getAll(Long ledgerId) {
		return transactionRepository.findByLedgerId(ledgerId).orElse(null);
	}

	public Transaction create(Transaction transaction) {
		return transactionRepository.save(transaction);
	}

	public boolean delete(Long id) {
		if (transactionRepository.existsById(id)) {
			transactionRepository.deleteById(id);
			return true;
		}
		return false;
	}
}