package ai.assistance.dtos.aiDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRequest {

    @NotBlank(message = "Please ask question")
    private String query;
    private UUID conversationId;
}
