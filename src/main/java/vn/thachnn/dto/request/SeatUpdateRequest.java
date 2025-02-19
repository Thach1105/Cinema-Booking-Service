package vn.thachnn.dto.request;

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

    @NotNull(message = "ids must be not null")
    private List<Long> ids;

    private SeatType type;
}
