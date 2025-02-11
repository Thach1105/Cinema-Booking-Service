package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thachnn.common.MovieStatus;
import vn.thachnn.dto.request.MovieRequest;
import vn.thachnn.dto.response.MovieResponse;
import vn.thachnn.exception.ResourceNotFoundException;
import vn.thachnn.mapper.MovieMapper;
import vn.thachnn.model.Movie;
import vn.thachnn.repository.MovieRepository;
import vn.thachnn.service.MovieService;

@Service
@Slf4j(topic = "MOVIE-SERVICE")
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MovieResponse save(MovieRequest request) {
        log.info("Saving movie: {}", request);

        Movie movie = movieMapper.toMovie(request);
        movie.setStatus(MovieStatus.NONE);

        Movie newMovie = movieRepository.save(movie);
        return movieMapper.toMovieResponse(newMovie);
    }

    @Override
    public MovieResponse findById(Long id) {
        log.info("Find movie by id: {}", id);
        Movie movie = getMovie(id);
        return movieMapper.toMovieResponse(movie);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MovieResponse update(MovieRequest request, Long movieId) {
        log.info("Updating movie: {}", request);

        //Get movie by id
        Movie movie = getMovie(movieId);
        Movie updatedMovie = movieMapper.toMovie(request);
        updatedMovie.setId(movieId);
        updatedMovie.setStatus(movie.getStatus());

        //update movie in database
        movieRepository.save(updatedMovie);
        return movieMapper.toMovieResponse(updatedMovie);
    }

    public void findAll(String keyword, String sort, int page, int size){
        log.info("Find all movie start: ");
    }

    private Movie getMovie(Long id){
        return movieRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Movie not found")
        );
    }
}
