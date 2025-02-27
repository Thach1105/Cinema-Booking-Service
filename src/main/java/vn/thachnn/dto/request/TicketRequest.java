package vn.thachnn.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketRequest implements Serializable {

    @NotNull(message = "showtimeId must be not null")
    private Long showtimeId;

    @Schema(description = "", example = "[101, 102, 103]")
    @NotNull(message = "showtimeSeatIds must be not null")
    @NotEmpty(message = "showtimeSeatIds must be not empty")
    private List<Long> seatIds;
}
