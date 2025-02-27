package vn.thachnn.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.thachnn.common.Gender;
import vn.thachnn.common.UserType;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class UserCreationRequest implements Serializable {

    @Schema(description = "The full name of the user", example = "Nguyễn Văn A")
    @NotBlank(message = "fullName must be not blank")
    private String fullName;

    @Schema(description = "The gender of the user", implementation = Gender.class)
    private Gender gender;

    @Schema(description = "The date of birth of the user", example = "2024-01-15")
    @NotNull(message = "dateOfBirth must be not null")
    private LocalDate dateOfBirth;

    @Schema(description = "The phone number of the user", example = "0987654312")
    private String phone;

    @Schema(description = "The email address of the user", example = "nguyenvana@example.com")
    @Email(message = "email invalid")
    private String email;

    @Schema(description = "The username for the user", example = "user1")
    @NotBlank(message = "username must be not blank")
    private String username;

    @Schema(description = "The password for the user", example = "password123")
    @NotBlank(message = "password must be not blank")
    private String password;
    private UserType type;
}
