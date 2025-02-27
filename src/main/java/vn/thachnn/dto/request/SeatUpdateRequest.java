package vn.thachnn.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.thachnn.common.SeatType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeatUpdateRequest implements Serializable {

    @Schema(description = "The list of seat IDs to be updated", example = "[1, 2, 3]")
    @NotNull(message = "ids must be not null")
    private List<Long> ids;

    @Schema(description = "The type of seat", implementation = SeatType.class)
    private SeatType type;
}
