package ai.assistance.errors.GlobalException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomErrorResponse {

    private LocalDateTime timestamp;
    private String path;
    private Error error;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Error {
        private String status;
        private String message;
    }
}