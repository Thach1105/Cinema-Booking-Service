package vn.thachnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import vn.thachnn.model.Cinema;
import vn.thachnn.model.CinemaHall;

import java.util.List;

@Repository
public interface CinemaHallRepository extends JpaRepository<CinemaHall, Long> {

    boolean existsByNameAndCinema(String name, Cinema cinema);

    List<CinemaHall> findAllByCinema(Cinema cinema);
}
