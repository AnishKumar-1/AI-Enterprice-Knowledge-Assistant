package ai.assistance.dtos.userDto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @NotBlank(message = "Required first name")
    @Size(min = 3,message = "Please enter at least 3 character")
    private String firstName;
    @NotBlank(message = "Required last name")
    @Size(min = 3,message = "Please enter at least 3 character")
    private String lastName;
    @NotBlank(message = "Required email")
    @Email(message = "Invalid email format")
    private String email;
    @Size(
            min = 8,
            max = 20,
            message = "Password must be between 8 and 20 characters"
    )
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain uppercase, lowercase, number and special character"
    )
    private String createPassword;
}
