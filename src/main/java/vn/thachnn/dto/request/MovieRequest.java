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
public class MovieRequest implements Serializable {

    @NotBlank(message = "title must be not blank")
    private String title;
    private String description;

    @NotNull(message = "duration must be not null")
    private Integer duration;
    private Integer ageLimit;

    @NotNull(message = "releaseDate must be not null")
    private LocalDate releaseDate;
}