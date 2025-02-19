package vn.thachnn.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import vn.thachnn.common.SeatType;

import java.io.Serializable;

@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_seat")
@AllArgsConstructor
@NoArgsConstructor
public class Seat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "cinema_hall_id")
    private CinemaHall hall;

    @Column(name = "seat_number")
    private String seatNumber;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private SeatType type;
}
