package WiseFox.Finance.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.dto.mapper.LedgerMapper;
import WiseFox.Finance.dto.request.LedgerRequest;
import WiseFox.Finance.dto.response.LedgerResponse;
import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.User;
import WiseFox.Finance.model.UserLedger;
import WiseFox.Finance.service.LedgerService;
import WiseFox.Finance.service.UserLedgerService;
import WiseFox.Finance.service.UserService;

import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;

@RestController
@RequestMapping("/api/ledgers")
public class LedgerController {

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserLedgerService userLedgerService;

    @GetMapping("user/{user_id}")
    public ResponseEntity<List<LedgerResponse>> getAllLedgers(@PathVariable Long user_id) {
        List<Ledger> ledgers = ledgerService.getMyLedgers(user_id);
        List<LedgerResponse> responses = ledgers.stream()
                .map(LedgerMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LedgerResponse> getLedgerById(@PathVariable Long id) {
        Ledger ledger = ledgerService.getById(id);
        return ResponseEntity.ok(LedgerMapper.toResponse(ledger));
    }

    @PostMapping("/create")
    public ResponseEntity<LedgerResponse> createLedger(@Valid @RequestBody LedgerRequest request) {
        if (StringUtils.isAnyBlank(request.getName(), request.getCurrency())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and currency are required.");
        }

        // 1. Fetch owner and persist the ledger
        User user = userService.getById(request.getUserId());
        Ledger ledger = LedgerMapper.toEntity(request, user);
        Ledger created = ledgerService.create(ledger);

        // 2. Register the creator as OWNER in user_ledger
        UserLedger userLedger = new UserLedger();
        userLedger.setUser(user);
        userLedger.setLedger(created);
        userLedgerService.create(userLedger); // sets Permission.OWNER internally

        return ResponseEntity.status(HttpStatus.CREATED).body(LedgerMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LedgerResponse> updateLedger(
            @PathVariable Long id,
            @Valid @RequestBody LedgerRequest request) {

        if (StringUtils.isAnyBlank(request.getName(), request.getCurrency())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and currency are required.");
        }
        Ledger existing = ledgerService.getById(id);
        LedgerMapper.updateEntityFromRequest(existing, request);
        Ledger updated = ledgerService.update(id, existing);
        return ResponseEntity.ok(LedgerMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLedger(@PathVariable Long id) {
        ledgerService.delete(id);
        return ResponseEntity.ok().build();
    }
}