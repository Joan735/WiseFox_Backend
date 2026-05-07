package WiseFox.Finance.service;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.Transaction;
import WiseFox.Finance.model.User;
import WiseFox.Finance.repository.LedgerRepository;
import WiseFox.Finance.repository.TransactionRepository;
import WiseFox.Finance.repository.UserLedgerRepository;
import WiseFox.Finance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

    @Mock private LedgerRepository ledgerRepository;
    @Mock private UserRepository userRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private UserLedgerRepository userLedgerRepository;

    @InjectMocks
    private LedgerService ledgerService;

    private User user;
    private Ledger ledger;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("johndoe");

        ledger = new Ledger();
        ledger.setId(1L);
        ledger.setName("My Ledger");
        ledger.setCurrency("EUR");
        ledger.setUser(user);
    }

    // ── GET MY LEDGERS ────────────────────────────────────────────────────────

    @Test
    void getMyLedgers_returnsList() {
        when(ledgerRepository.findByUserId(1L)).thenReturn(Optional.of(List.of(ledger)));

        List<Ledger> result = ledgerService.getMyLedgers(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("My Ledger");
    }

    @Test
    void getMyLedgers_noLedgers_returnsEmpty() {
        when(ledgerRepository.findByUserId(1L)).thenReturn(Optional.empty());

        List<Ledger> result = ledgerService.getMyLedgers(1L);

        assertThat(result).isEmpty();
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────

    @Test
    void getById_found() {
        when(ledgerRepository.findById(1L)).thenReturn(Optional.of(ledger));

        Ledger result = ledgerService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_notFound_throws() {
        when(ledgerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ledgerService.getById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ledger not found");
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    void create_success() {
        when(ledgerRepository.save(ledger)).thenReturn(ledger);

        Ledger result = ledgerService.create(ledger);

        assertThat(result.getName()).isEqualTo("My Ledger");
        verify(ledgerRepository).save(ledger);
    }

    @Test
    void create_withoutUser_throws() {
        ledger.setUser(null);

        assertThatThrownBy(() -> ledgerService.create(ledger))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User is required");
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Test
    void update_success() {
        Ledger updated = new Ledger();
        updated.setName("Updated Ledger");
        updated.setCurrency("USD");
        updated.setDescription("New desc");

        when(ledgerRepository.findById(1L)).thenReturn(Optional.of(ledger));
        when(ledgerRepository.save(any(Ledger.class))).thenAnswer(i -> i.getArgument(0));

        Ledger result = ledgerService.update(1L, updated);

        assertThat(result.getName()).isEqualTo("Updated Ledger");
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void update_notFound_throws() {
        when(ledgerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ledgerService.update(99L, ledger))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ledger not found");
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    void delete_success_removesTransactionsAndMembers() {
        Transaction tx = new Transaction();
        tx.setId(10L);

        when(ledgerRepository.findById(1L)).thenReturn(Optional.of(ledger));
        when(transactionRepository.findByLedgerId(1L)).thenReturn(Optional.of(List.of(tx)));

        ledgerService.delete(1L);

        verify(transactionRepository).deleteAllById(List.of(10L));
        verify(userLedgerRepository).deleteByLedger(ledger);
        verify(ledgerRepository).delete(ledger);
    }

    @Test
    void delete_noTransactions_stillDeletesLedger() {
        when(ledgerRepository.findById(1L)).thenReturn(Optional.of(ledger));
        when(transactionRepository.findByLedgerId(1L)).thenReturn(Optional.of(Collections.emptyList()));

        ledgerService.delete(1L);

        verify(transactionRepository, never()).deleteAllById(any());
        verify(ledgerRepository).delete(ledger);
    }

    @Test
    void delete_notFound_throws() {
        when(ledgerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ledgerService.delete(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ledger not found");
    }
}
