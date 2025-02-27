package vn.thachnn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.thachnn.model.Movie;
import vn.thachnn.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("""
            SELECT r FROM Review r
            WHERE r.movie = :movie
            AND (
                :rating IS NULL
                OR r.rating = :rating
            )
            ORDER BY r.updatedAt DESC
            """)
    Page<Review> findAllReview(Movie movie, Pageable pageable, Integer rating);
}
