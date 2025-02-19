package vn.thachnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.thachnn.model.Cinema;
import vn.thachnn.model.CinemaHall;
import vn.thachnn.model.Movie;
import vn.thachnn.model.Showtime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query(value = """
            SELECT COUNT(s) > 0 FROM Showtime s
            WHERE s.hall = :hall
            AND (
                (:startTime BETWEEN s.startTime AND s.endTime)
                OR (:endTime BETWEEN s.startTime AND s.endTime)
                OR (s.startTime BETWEEN :startTime AND :endTime)
            )
            """)
    boolean checkOverlappingShowtime(CinemaHall hall, LocalDateTime startTime, LocalDateTime endTime);

    @Query("""
            SELECT s FROM Showtime s
            WHERE s.startTime > CURRENT_TIMESTAMP
            AND s.hall.cinema = :cinema
            AND (
                :movie IS NULL
                OR s.movie = :movie
            )
            AND (
                :selectedDate IS NULL
                OR DATE(s.startTime) = :selectedDate
            )
            ORDER BY s.startTime ASC
            """)
    List<Showtime> getAllShowtimeForUser(Cinema cinema, Movie movie, LocalDate selectedDate);
}
