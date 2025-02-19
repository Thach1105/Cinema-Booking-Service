package vn.thachnn.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import vn.thachnn.common.SeatStatus;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "showtime_seat")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShowtimeSeats implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Seat seat;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatStatus status;
}
