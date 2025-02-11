package vn.thachnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.thachnn.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
}
