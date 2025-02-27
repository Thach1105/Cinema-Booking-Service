package vn.thachnn.dto.response;

import lombok.*;
import vn.thachnn.common.Gender;
import vn.thachnn.common.UserStatus;
import vn.thachnn.common.UserType;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {

    private Long id;
    private String username;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserType type;
    private UserStatus status;
}
