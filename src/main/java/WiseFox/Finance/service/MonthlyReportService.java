package WiseFox.Finance.service;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.Transaction;
import WiseFox.Finance.model.Transaction.Category;
import WiseFox.Finance.model.Transaction.TransactionType;
import WiseFox.Finance.model.User;
import WiseFox.Finance.repository.LedgerRepository;
import WiseFox.Finance.repository.TransactionRepository;
import WiseFox.Finance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MonthlyReportService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LedgerRepository ledgerRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private EmailService emailService;

	// Runs at 08:00 on the 1st of every month
	// For every 1 min @Scheduled(cron = "0 * * * * *")
	@Scheduled(cron = "0 0 8 1 * *")
	public void sendMonthlyReports() {
		LocalDate today = LocalDate.now();
		LocalDate firstDay = today.minusMonths(1).withDayOfMonth(1);
		LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
		String monthLabel = firstDay.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " "
				+ firstDay.getYear();

		Iterable<User> users = userRepository.findAll();
		for (User user : users) {
			List<Ledger> ledgers = ledgerRepository.findByUserId(user.getId()).orElse(Collections.emptyList());

			if (ledgers.isEmpty())
				continue;

			List<LedgerSummary> summaries = ledgers.stream().map(l -> buildSummary(l, firstDay, lastDay)).filter(
					s -> s.totalIncome.compareTo(BigDecimal.ZERO) > 0 || s.totalExpense.compareTo(BigDecimal.ZERO) > 0)
					.toList();

			if (!summaries.isEmpty()) {
				emailService.sendMonthlyReport(user.getEmail(), user.getName(), monthLabel, summaries);
			}
		}
	}

	// -----------------------------------------------------------------------
	// Build summary for one ledger
	// -----------------------------------------------------------------------
	private LedgerSummary buildSummary(Ledger ledger, LocalDate start, LocalDate end) {
		List<Transaction> txs = transactionRepository.findByLedgerIdAndDateBetween(ledger.getId(), start, end);

		BigDecimal income = sum(txs, TransactionType.INCOME);
		BigDecimal expense = sum(txs, TransactionType.EXPENSE);

		Map<Category, BigDecimal> byCategory = txs.stream().filter(t -> t.getCategory() != null)
				.collect(Collectors.groupingBy(Transaction::getCategory,
						Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

		return new LedgerSummary(ledger.getName(), ledger.getCurrency(), income, expense, byCategory);
	}

	private BigDecimal sum(List<Transaction> txs, TransactionType type) {
		return txs.stream().filter(t -> t.getType() == type).map(Transaction::getAmount).reduce(BigDecimal.ZERO,
				BigDecimal::add);
	}

	// -----------------------------------------------------------------------
	// DTO
	// -----------------------------------------------------------------------
	public record LedgerSummary(String ledgerName, String currency, BigDecimal totalIncome, BigDecimal totalExpense,
			Map<Category, BigDecimal> byCategory) {
		public BigDecimal net() {
			return totalIncome.subtract(totalExpense);
		}
	}
}