package WiseFox.Finance.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLedgerRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Ledger ID is required")
    private Long ledgerId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(Long ledgerId) {
        this.ledgerId = ledgerId;
    }
}