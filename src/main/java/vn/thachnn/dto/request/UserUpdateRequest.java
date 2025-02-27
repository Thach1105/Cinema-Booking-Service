package vn.thachnn.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.thachnn.common.Gender;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class UserUpdateRequest implements Serializable {

    @Schema(description = "The ID of the user to update", example = "1")
    @NotNull(message = "id must be not null")
    private Long id;

    @Schema(description = "The full name of the user", example = "Nguyễn Văn A")
    @NotBlank(message = "fullName must be not blank")
    private String fullName;

    @Schema(description = "The gender of the user", implementation = Gender.class)
    private Gender gender;

    @Schema(description = "The date of birth of the user", example = "2025-05-15")
    private LocalDate dateOfBirth;

    @Schema(description = "The phone number of the user", example = "0987654123")
    private String phone;
/*    private String email;*/
}
