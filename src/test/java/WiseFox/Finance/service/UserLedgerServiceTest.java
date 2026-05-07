package WiseFox.Finance.service;

import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.User;
import WiseFox.Finance.model.UserLedger;
import WiseFox.Finance.model.UserLedger.Permission;
import WiseFox.Finance.repository.AuthRepository;
import WiseFox.Finance.repository.LedgerRepository;
import WiseFox.Finance.repository.UserLedgerRepository;
import WiseFox.Finance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLedgerServiceTest {

    @Mock private UserLedgerRepository userLedgerRepository;
    @Mock private UserRepository userRepository;
    @Mock private LedgerRepository ledgerRepository;
    @Mock private AuthRepository authRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private UserLedgerService userLedgerService;

    private User owner;
    private User member;
    private Ledger ledger;
    private UserLedger userLedger;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        member = new User();
        member.setId(2L);
        member.setName("Member");
        member.setEmail("member@example.com");

        ledger = new Ledger();
        ledger.setId(1L);
        ledger.setName("Shared Ledger");

        userLedger = new UserLedger();
        userLedger.setUser(owner);
        userLedger.setLedger(ledger);
    }

    // ── CREATE (OWNER) ────────────────────────────────────────────────────────

    @Test
    void create_success_setsOwnerPermission() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(ledgerRepository.existsById(1L)).thenReturn(true);
        when(userLedgerRepository.existsByUserAndLedger(owner, ledger)).thenReturn(false);
        when(userLedgerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserLedger result = userLedgerService.create(userLedger);

        assertThat(result.getPermission()).isEqualTo(Permission.OWNER);
    }

    @Test
    void create_alreadyAssigned_throwsConflict() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(ledgerRepository.existsById(1L)).thenReturn(true);
        when(userLedgerRepository.existsByUserAndLedger(owner, ledger)).thenReturn(true);

        assertThatThrownBy(() -> userLedgerService.create(userLedger))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already assigned");
    }

    // ── SHARE (MEMBER) ────────────────────────────────────────────────────────

    @Test
    void share_success_setsMemberPermission() {
        userLedger.setUser(member);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(ledgerRepository.existsById(1L)).thenReturn(true);
        when(userLedgerRepository.existsByUserAndLedger(member, ledger)).thenReturn(false);
        when(userLedgerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserLedger result = userLedgerService.share(userLedger);

        assertThat(result.getPermission()).isEqualTo(Permission.MEMBER);
    }

    // ── SHARE BY EMAIL ────────────────────────────────────────────────────────

    @Test
    void shareByEmail_success() {
        when(authRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(ledgerRepository.findById(1L)).thenReturn(Optional.of(ledger));
        when(userLedgerRepository.existsByUserAndLedger(member, ledger)).thenReturn(false);
        when(userLedgerRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(emailService).sendLedgerSharedNotification(any(), any(), any(), any());

        UserLedger result = userLedgerService.shareByEmail(1L, 1L, "member@example.com");

        assertThat(result.getPermission()).isEqualTo(Permission.MEMBER);
        verify(emailService).sendLedgerSharedNotification(
                eq("member@example.com"), eq("Member"), eq("Owner"), eq("Shared Ledger"));
    }

    @Test
    void shareByEmail_recipientNotFound_throws() {
        when(authRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userLedgerService.shareByEmail(1L, 1L, "ghost@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("No user found with email");
    }

    @Test
    void shareByEmail_shareWithSelf_throws() {
        when(authRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(ledgerRepository.findById(1L)).thenReturn(Optional.of(ledger));

        assertThatThrownBy(() -> userLedgerService.shareByEmail(1L, 1L, "owner@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("cannot share a ledger with yourself");
    }

    @Test
    void shareByEmail_alreadyMember_throwsConflict() {
        when(authRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(ledgerRepository.findById(1L)).thenReturn(Optional.of(ledger));
        when(userLedgerRepository.existsByUserAndLedger(member, ledger)).thenReturn(true);

        assertThatThrownBy(() -> userLedgerService.shareByEmail(1L, 1L, "member@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already a member");
    }

    @Test
    void shareByEmail_blankEmail_throws() {
        assertThatThrownBy(() -> userLedgerService.shareByEmail(1L, 1L, "  "))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("targetEmail is required");
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    void deleteByUserAndLedger_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(ledgerRepository.findById(1L)).thenReturn(Optional.of(ledger));
        when(userLedgerRepository.existsByUserAndLedger(owner, ledger)).thenReturn(true);

        userLedgerService.deleteByUserAndLedger(1L, 1L);

        verify(userLedgerRepository).deleteByUserAndLedger(owner, ledger);
    }

    @Test
    void deleteByUserAndLedger_notAssigned_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(ledgerRepository.findById(1L)).thenReturn(Optional.of(ledger));
        when(userLedgerRepository.existsByUserAndLedger(owner, ledger)).thenReturn(false);

        assertThatThrownBy(() -> userLedgerService.deleteByUserAndLedger(1L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not assigned");
    }
}
