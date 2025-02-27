package vn.thachnn.model;

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
@Entity
@Table(
        name = "ticket_price",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"showtime_type", "room_type", "seat_type", "day_type"}
        )
)
@AllArgsConstructor
@NoArgsConstructor
public class TicketPrice implements Serializable {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "showtime_type", nullable = false)
    private ShowtimeType showtimeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_type", nullable = false)
    private DayType dayType;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    @Column(name = "price", nullable = false)
    private Integer price;
}
