package WiseFox.Finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

@RestController
@RequestMapping("/api/userledger")
public class UserLedgerController {
    
    @Autowired
    private UserLedgerService userledgerService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LedgerService ledgerService;

    @PostMapping("/create")
    public ResponseEntity<UserLedgerResponse> createUserLedger(@Valid @RequestBody UserLedgerRequest request) {
        try {
            User user = userService.getById(request.getUserId());
            Ledger ledger = ledgerService.getById(request.getLedgerId());
            
            UserLedger userLedger = UserLedgerMapper.toEntity(request, user, ledger);
            UserLedger createdUserLedger = userledgerService.create(userLedger);
            
            UserLedgerResponse response = UserLedgerMapper.toResponse(createdUserLedger);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (ResponseStatusException e) {
            System.err.println("ERROR: " + e);
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @PostMapping("/share")
    public ResponseEntity<UserLedgerResponse> shareLedger(@Valid @RequestBody UserLedgerRequest request) {
        try {
            User user = userService.getById(request.getUserId());
            Ledger ledger = ledgerService.getById(request.getLedgerId());
            
            UserLedger userLedger = UserLedgerMapper.toEntity(request, user, ledger);
            UserLedger sharedUserLedger = userledgerService.share(userLedger);
            
            UserLedgerResponse response = UserLedgerMapper.toResponse(sharedUserLedger);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (ResponseStatusException e) {
            System.err.println("ERROR: " + e);
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteLedger(@RequestParam Long userId, @RequestParam Long ledgerId) {
        try {
            userledgerService.deleteByUserAndLedger(userId, ledgerId);	
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            System.err.println("ERROR: " + e);
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}