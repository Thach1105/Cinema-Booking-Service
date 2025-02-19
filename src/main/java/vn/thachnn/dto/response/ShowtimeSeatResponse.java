package vn.thachnn.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import vn.thachnn.common.SeatStatus;
import vn.thachnn.common.SeatType;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowtimeSeatResponse implements Serializable {

    @JsonProperty("ShowtimeSeatId")
    private Long id;
    private String seatNumber;
    private SeatType seatType;
    private SeatStatus status;
}
