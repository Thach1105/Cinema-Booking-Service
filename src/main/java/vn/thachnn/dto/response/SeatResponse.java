package vn.thachnn.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.thachnn.common.SeatType;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeatResponse implements Serializable {

    private Long id;

    @JsonProperty("cinemaHall_id")
    private Long cinemaHallId;
    private String seatNumber;
    private SeatType type;
}
