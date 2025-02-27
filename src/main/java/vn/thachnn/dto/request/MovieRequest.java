package vn.thachnn.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.thachnn.common.MovieStatus;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class MovieRequest implements Serializable {

    @Schema(description = "The title of the movie", example = "Avengers: Endgame")
    @NotBlank(message = "title must be not blank")
    private String title;

    @Schema(description = "A brief description of the movie")
    private String description;

    @Schema(description = "The duration of the movie in minutes", example = "180")
    @Min(value = 1, message = "duration must be equal or greater than 1")
    @NotNull(message = "duration must be not null")
    private Integer duration;


    @Schema(description = "The age limit for the movie", example = "13")
    private Integer ageLimit;

    @Schema(description = "The release date of the movie", example = "2025-03-15")
    @NotNull(message = "releaseDate must be not null")
    private LocalDate releaseDate;

    @Schema(description = "URL to the movie's trailer", example = "https://www.youtube.com/watch?v=abc123")
    private String trailer;

    @Schema(description = "The status of the movie", implementation = MovieStatus.class)
    private MovieStatus status;
}