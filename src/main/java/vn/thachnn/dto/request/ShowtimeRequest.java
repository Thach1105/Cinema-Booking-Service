package vn.thachnn.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimeRequest implements Serializable {

    @Schema(description = "The ID of the movie for this showtime", example = "10")
    @NotNull(message = "movieId must be not null")
    @Min(value = 1, message = "movieId must be greater than or equal to 1")
    private Long movieId;

    @Schema(description = "The ID of the hall where the movie will be shown", example = "5")
    @Min(value = 1, message = "hallId must be greater than or equal to 1")
    @NotNull(message = "hallId must be not null")
    private Long hallId;

    @Schema(description = "The start time of the showtime (must be in the future)", example = "2025-03-15T18:30:00")
    @Future(message = "The showtime start time must be in the future")
    @NotNull(message = "startTime must be not null")
    private LocalDateTime startTime;

    @Schema(description = "The end time of the showtime", example = "2025-03-15T21:00:00")
    @Future(message = "The showtime start time must be in the future")
    @NotNull(message = "endTime must be not null")
    private LocalDateTime endTime;

    /*@Min(value = 0, message = "price must be greater than or equal to 0")
    @NotNull(message = "price must be not null")
    private Integer price;*/
}
