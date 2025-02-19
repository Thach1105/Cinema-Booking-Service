package vn.thachnn.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import vn.thachnn.common.RoomType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "cinema_hall")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CinemaHall implements Serializable {

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
    @JoinColumn(name = "cinema_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cinema cinema;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "number_row")
    private Integer row;

    @Column(name = "number_column")
    private Integer column;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RoomType type;

    @Column(name = "available")
    private boolean available;

    @OneToMany(mappedBy = "hall", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Seat> seats;

    @PrePersist
    @PreUpdate
    private void calculateTotalSeats() {
        if(row != null && column != null){
            this.totalSeats = row * column;
        }
    }
}
