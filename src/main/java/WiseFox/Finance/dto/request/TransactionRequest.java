package WiseFox.Finance.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import WiseFox.Finance.model.Transaction.TransactionType;
import WiseFox.Finance.model.Transaction.Category;

import java.time.LocalDate;

@Data
public class TransactionRequest {
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    @NotNull(message = "Transaction type is required")
    private TransactionType type;
    
    private Category category;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    private String note;
    
    @NotNull(message = "Ledger ID is required")
    private Long ledgerId;

    // 手动添加 Getter 和 Setter
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(Long ledgerId) {
        this.ledgerId = ledgerId;
    }
}