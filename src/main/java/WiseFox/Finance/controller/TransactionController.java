package WiseFox.Finance.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.dto.mapper.TransactionMapper;
import WiseFox.Finance.dto.request.TransactionRequest;
import WiseFox.Finance.dto.response.TransactionResponse;
import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.Transaction;
import WiseFox.Finance.service.LedgerService;
import WiseFox.Finance.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private LedgerService ledgerService;

    @GetMapping("/{ledgerId}")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(@PathVariable Long ledgerId) {
        List<Transaction> transactions = transactionService.getAll(ledgerId);
        List<TransactionResponse> responses = transactions.stream()
                .map(TransactionMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/create")
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        if (request.getAmount() == null || request.getDate() == null
                || request.getType() == null || request.getLedgerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount, date, type and ledgerId are required.");
        }
        Ledger ledger = ledgerService.getById(request.getLedgerId());
        Transaction transaction = TransactionMapper.toEntity(request, ledger);
        Transaction created = transactionService.create(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionMapper.toResponse(created));
    }

    @DeleteMapping("/delete/{transactionId}")
    public ResponseEntity<Void> delete(@PathVariable Long transactionId) {
        transactionService.delete(transactionId);
        return ResponseEntity.ok().build();
    }
}