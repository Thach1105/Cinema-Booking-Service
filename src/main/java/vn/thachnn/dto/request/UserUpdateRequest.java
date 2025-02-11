package vn.thachnn.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class UserUpdateRequest implements Serializable {

    @NotNull(message = "id must be not null")
    private Long id;

    @NotBlank(message = "fullName must be not blank")
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private String phone;
/*    private String email;*/
}
