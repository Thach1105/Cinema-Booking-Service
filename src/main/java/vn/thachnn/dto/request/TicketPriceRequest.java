package vn.thachnn.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.thachnn.common.DayType;
import vn.thachnn.common.RoomType;
import vn.thachnn.common.SeatType;
import vn.thachnn.common.ShowtimeType;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketPriceRequest implements Serializable {

    @Schema(description = "Type of showtime", implementation = ShowtimeType.class)
    private ShowtimeType showtimeType;

    @Schema(description = "Type of day", implementation = DayType.class)
    private DayType dayType;

    @Schema(description = "Type of room", implementation = RoomType.class)
    private RoomType roomType;

    @Schema(description = "Type of seat", implementation = SeatType.class)
    private SeatType seatType;

    @Schema(description = "Price of the ticket", example = "100000")
    @Min(value = 0, message = "price must be greater than or equal to 0")
    @NotNull(message = "price must be not null")
    private Integer price;
}
