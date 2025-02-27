package vn.thachnn.dto.response;

import jakarta.persistence.*;
import lombok.*;
import vn.thachnn.common.DayType;
import vn.thachnn.common.RoomType;
import vn.thachnn.common.SeatType;
import vn.thachnn.common.ShowtimeType;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketPriceResponse implements Serializable {

    private Long id;
    private ShowtimeType showtimeType;
    private DayType dayType;
    private RoomType roomType;
    private SeatType seatType;
    private Integer price;
}
