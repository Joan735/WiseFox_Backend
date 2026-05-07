package WiseFox.Finance.service;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.Transaction;
import WiseFox.Finance.model.Transaction.TransactionType;
import WiseFox.Finance.repository.LedgerRepository;
import WiseFox.Finance.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private LedgerRepository ledgerRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Ledger ledger;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        ledger = new Ledger();
        ledger.setId(1L);
        ledger.setName("My Ledger");

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.valueOf(100.0));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setDate(LocalDate.now());
        transaction.setLedger(ledger);
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────

    @Test
    void getAll_returnsList() {
        when(transactionRepository.findByLedgerId(1L)).thenReturn(Optional.of(List.of(transaction)));

        List<Transaction> result = transactionService.getAll(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAll_noTransactions_returnsEmpty() {
        when(transactionRepository.findByLedgerId(1L)).thenReturn(Optional.empty());

        List<Transaction> result = transactionService.getAll(1L);

        assertThat(result).isEmpty();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    void create_success() {
        when(ledgerRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = transactionService.create(transaction);

        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100.0));
        verify(transactionRepository).save(transaction);
    }

    @Test
    void create_withoutLedger_throws() {
        transaction.setLedger(null);

        assertThatThrownBy(() -> transactionService.create(transaction))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ledger is required");
    }

    @Test
    void create_ledgerNotFound_throws() {
        when(ledgerRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> transactionService.create(transaction))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ledger doesn't exist");
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    void delete_success() {
        when(transactionRepository.existsById(1L)).thenReturn(true);

        transactionService.delete(1L);

        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(transactionRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> transactionService.delete(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Transaction not found");
    }
}
