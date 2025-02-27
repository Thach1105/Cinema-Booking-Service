package vn.thachnn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.thachnn.model.Cinema;

import java.util.List;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {

    @Query(value = "SELECT c FROM Cinema c WHERE c.city LIKE :city")
    Page<Cinema> searchByCity (String city, Pageable pageable);

    @Query("""
            SELECT DISTINCT c.city FROM Cinema c
            """)
    List<String> getAllCities();
}
