package WiseFox.Finance.dto.response;

import WiseFox.Finance.model.Transaction.TransactionType;
import WiseFox.Finance.model.Transaction.Category;

import java.time.LocalDate;

public record TransactionResponse(
    Long id,
    Double amount,
    TransactionType type,
    Category category,
    LocalDate date,
    String note,
    Long ledgerId,
    String ledgerName
) {}