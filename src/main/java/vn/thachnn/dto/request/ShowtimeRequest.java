package vn.thachnn.dto.request;

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

    private Long id;

    @NotNull(message = "movieId must be not null")
    @Min(value = 1, message = "movieId must be greater than or equal to 1")
    private Long movieId;

    @Min(value = 1, message = "hallId must be greater than or equal to 1")
    @NotNull(message = "hallId must be not null")
    private Long hallId;

    @NotNull(message = "startTime must be not null")
    private LocalDateTime startTime;

    @NotNull(message = "endTime must be not null")
    private LocalDateTime endTime;

    @Min(value = 0, message = "price must be greater than or equal to 0")
    @NotNull(message = "price must be not null")
    private Integer price;
}
