package vn.thachnn.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class UserPasswordRequest implements Serializable {

    @NotBlank(message = "password must be not blank")
    private String password;

    @NotBlank(message = "confirmPassword be not blank")
    protected String confirmPassword;
}
