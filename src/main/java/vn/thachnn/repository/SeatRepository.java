package vn.thachnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.thachnn.model.CinemaHall;
import vn.thachnn.model.Seat;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query(value = "SELECT s FROM Seat s WHERE s.hall = :hall ")
    List<Seat> findAllByHall(CinemaHall hall);


}
