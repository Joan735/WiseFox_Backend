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
    // Body: { "ownerUserId": Long, "ledgerId": Long, "targetEmail": String }
    //
    // Flow lives entirely in UserLedgerService.shareByEmail() so that the
    // user_ledger insert + notification email run inside the same transaction.
    // If the email send fails, the membership row is rolled back automatically
    // and the client receives a 500.
    @PostMapping("/share-by-email")
    public ResponseEntity<?> shareLedgerByEmail(
            @RequestBody Map<String, Object> body) {
        try {
            if (body.get("ownerUserId") == null || body.get("ledgerId") == null
                    || body.get("targetEmail") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "ownerUserId, ledgerId and targetEmail are required"));
            }

            Long ownerUserId = Long.valueOf(body.get("ownerUserId").toString());
            Long ledgerId    = Long.valueOf(body.get("ledgerId").toString());
            String email     = body.get("targetEmail").toString();

            UserLedger shared = userledgerService.shareByEmail(ownerUserId, ledgerId, email);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UserLedgerMapper.toResponse(shared));

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "ownerUserId and ledgerId must be numeric"));

        } catch (ResponseStatusException e) {
            // Known status codes from the service layer (404, 409, 400, ...)
            System.err.println("share-by-email error: " + e.getStatusCode() + " " + e.getReason());
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getReason() == null ? "" : e.getReason()));

        } catch (RuntimeException e) {
            // Email send failure (or anything else unexpected) — transaction
            // is already rolled back at this point.
            System.err.println("share-by-email failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error",
                            "Could not share the ledger. Please try again later."));
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