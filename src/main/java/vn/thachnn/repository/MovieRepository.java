package vn.thachnn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.thachnn.common.MovieStatus;
import vn.thachnn.model.Movie;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    @Query(value = """
            SELECT m FROM Movie m
            WHERE :status <> 'NONE'
            AND m.status = :status
            """)
    Page<Movie> getListMoviePublic(MovieStatus status, Pageable pageable);
}
