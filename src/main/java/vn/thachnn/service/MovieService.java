package vn.thachnn.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import vn.thachnn.common.MovieStatus;
import vn.thachnn.dto.request.MovieRequest;
import vn.thachnn.dto.response.MovieResponse;
import vn.thachnn.model.Movie;

import java.time.LocalDate;

public interface MovieService {

    MovieResponse save(MovieRequest request, MultipartFile movieBanner);

    MovieResponse findById(Long id);

    MovieResponse update(MovieRequest request, Long movieId);

    void updateMovieBanner(Long movieId, MultipartFile newBanner);

    Page<Movie> getListMoviePublic(int pageNumber, int pageSize, MovieStatus status);

    Page<Movie> findAll(
            String title, LocalDate releaseDate, Integer minDuration, Integer maxDuration,
            String ageLimitCondition, MovieStatus status,
            String sortBy, String direction, int pageNumber, int pageSize
    );

    Movie getMovie(Long id);

    void updateMovieStatus(Long movieId, MovieStatus status);
}
