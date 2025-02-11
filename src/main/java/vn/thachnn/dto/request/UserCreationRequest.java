package vn.thachnn.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.thachnn.common.UserType;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class UserCreationRequest implements Serializable {

    @NotBlank(message = "fullName must be not blank")
    private String fullName;
    private String gender;

    @NotNull(message = "dateOfBirth must be not null")
    private LocalDate dateOfBirth;
    private String phone;

    @Email(message = "email invalid")
    private String email;

    @NotBlank(message = "username must be not blank")
    private String username;

    @NotBlank(message = "password must be not blank")
    private String password;
    private UserType type;
}
