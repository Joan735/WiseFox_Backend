package WiseFox.Finance.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class LedgerSharedRequest {

    @NotBlank(message = "Ledger name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Currency is required")
    @Size(max = 100)
    private String currency;

    private String description;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "At least one member username is required")
    private List<String> memberUsernames;  // 要共享的用户名列表（至少1个）

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<String> getMemberUsernames() { return memberUsernames; }
    public void setMemberUsernames(List<String> memberUsernames) { this.memberUsernames = memberUsernames; }
}