package WiseFox.Finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.dto.mapper.UserLedgerMapper;
import WiseFox.Finance.dto.request.UserLedgerRequest;
import WiseFox.Finance.dto.response.UserLedgerResponse;
import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.User;
import WiseFox.Finance.model.UserLedger;
import WiseFox.Finance.service.LedgerService;
import WiseFox.Finance.service.UserLedgerService;
import WiseFox.Finance.service.UserService;
import WiseFox.Finance.repository.AuthRepository;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/userledger")
public class UserLedgerController {

    @Autowired
    private UserLedgerService userledgerService;

    @Autowired
    private UserService userService;

    @Autowired
    private LedgerService ledgerService;

    // FIX #6: added to resolve the Android call to POST /api/userledger/share-by-email
    @Autowired
    private AuthRepository authRepository;

    // ── CREATE (owner) ─────────────────────────────────────────────────────────
    @PostMapping("/create")
    public ResponseEntity<UserLedgerResponse> createUserLedger(
            @Valid @RequestBody UserLedgerRequest request) {
        try {
            User user     = userService.getById(request.getUserId());
            Ledger ledger = ledgerService.getById(request.getLedgerId());

            UserLedger userLedger = UserLedgerMapper.toEntity(request, user, ledger);
            UserLedger created    = userledgerService.create(userLedger);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UserLedgerMapper.toResponse(created));

        } catch (ResponseStatusException e) {
            System.err.println("ERROR: " + e);
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    // ── SHARE (member, by userId+ledgerId) ────────────────────────────────────
    @PostMapping("/share")
    public ResponseEntity<UserLedgerResponse> shareLedger(
            @Valid @RequestBody UserLedgerRequest request) {
        try {
            User user     = userService.getById(request.getUserId());
            Ledger ledger = ledgerService.getById(request.getLedgerId());

            UserLedger userLedger  = UserLedgerMapper.toEntity(request, user, ledger);
            UserLedger shared      = userledgerService.share(userLedger);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UserLedgerMapper.toResponse(shared));

        } catch (ResponseStatusException e) {
            System.err.println("ERROR: " + e);
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    // ── SHARE BY EMAIL ────────────────────────────────────────────────────────
    // FIX #6: This endpoint is called by the Android app.
    // Body: { "ownerUserId": Long, "ledgerId": Long, "targetEmail": String }
    @PostMapping("/share-by-email")
    public ResponseEntity<?> shareLedgerByEmail(
            @RequestBody Map<String, Object> body) {
        try {
            Long ownerUserId = Long.valueOf(body.get("ownerUserId").toString());
            Long ledgerId    = Long.valueOf(body.get("ledgerId").toString());
            String email     = body.get("targetEmail").toString().trim();

            if (email.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "targetEmail is required"));
            }

            // Verify the ledger exists and the owner has it
            Ledger ledger = ledgerService.getById(ledgerId);

            // Look up recipient by email
            User recipient = authRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "No user found with email: " + email));

            // Don't share with yourself
            if (recipient.getId().equals(ownerUserId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "You cannot share a ledger with yourself"));
            }

            // Create the UserLedger entry for the recipient as MEMBER
            UserLedger userLedger = new UserLedger();
            userLedger.setUser(recipient);
            userLedger.setLedger(ledger);

            UserLedger shared = userledgerService.share(userLedger);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UserLedgerMapper.toResponse(shared));

        } catch (ResponseStatusException e) {
            System.err.println("ERROR share-by-email: " + e);
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            System.err.println("ERROR share-by-email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ── DELETE ─────────────────────────────────────────────────────────────────
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteLedger(
            @RequestParam Long userId, @RequestParam Long ledgerId) {
        try {
            userledgerService.deleteByUserAndLedger(userId, ledgerId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            System.err.println("ERROR: " + e);
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}