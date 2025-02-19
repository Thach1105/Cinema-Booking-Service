package vn.thachnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.thachnn.model.Showtime;
import vn.thachnn.model.ShowtimeSeats;

import java.util.List;

@Repository
public interface ShowtimeSeatsRepository extends JpaRepository<ShowtimeSeats, Long> {

    @Query("""
            SELECT ss FROM ShowtimeSeats ss
            WHERE ss.showtime = :showtime
            ORDER BY ss.seat.id ASC
            """)
    List<ShowtimeSeats> findAllByShowtime(Showtime showtime);
}
