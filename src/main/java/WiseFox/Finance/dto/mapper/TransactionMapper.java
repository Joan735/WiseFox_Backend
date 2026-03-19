package WiseFox.Finance.dto.mapper;

import WiseFox.Finance.dto.request.TransactionRequest;
import WiseFox.Finance.dto.response.TransactionResponse;
import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.Transaction;

import java.math.BigDecimal;

public class TransactionMapper {
    
    public static TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) return null;
        
        Long ledgerId = null;
        String ledgerName = null;
        
        if (transaction.getLedger() != null) {
            ledgerId = transaction.getLedger().getId();
            ledgerName = transaction.getLedger().getName();
        }
        
        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount() != null ? transaction.getAmount().doubleValue() : null,
            transaction.getType(),
            transaction.getCategory(),
            transaction.getDate(),
            transaction.getNote(),
            ledgerId,
            ledgerName
        );
    }
    
    public static Transaction toEntity(TransactionRequest request, Ledger ledger) {
        if (request == null) return null;
        
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount() != null ? 
            BigDecimal.valueOf(request.getAmount()) : null);
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setNote(request.getNote());
        transaction.setLedger(ledger);
        return transaction;
    }
    
    public static void updateEntityFromRequest(Transaction transaction, TransactionRequest request) {
        if (request == null || transaction == null) return;
        
        transaction.setAmount(request.getAmount() != null ? 
            BigDecimal.valueOf(request.getAmount()) : null);
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setNote(request.getNote());
    }
}