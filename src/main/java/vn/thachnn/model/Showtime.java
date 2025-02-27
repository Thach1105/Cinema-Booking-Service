package vn.thachnn.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "show_time")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Showtime implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "cinema_hall_id", nullable = false)
    private CinemaHall hall;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /*@Column(name = "price", nullable = false)
    private Integer price;*/

    @OneToMany(mappedBy = "showtime",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    @ToString.Exclude
    private Set<ShowtimeSeats> showtimeSeats;
}
