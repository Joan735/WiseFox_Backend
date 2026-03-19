package WiseFox.Finance.dto.response;

import java.time.LocalDateTime;

public record ApiErrorResponse(
    int status,
    String error,
    String message,
    String path,
    LocalDateTime timestamp
) {
    public ApiErrorResponse {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}